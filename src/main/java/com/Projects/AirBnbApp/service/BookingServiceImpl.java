package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.BookingDto;
import com.Projects.AirBnbApp.dto.BookingRequest;
import com.Projects.AirBnbApp.dto.GuestDto;
import com.Projects.AirBnbApp.entity.*;
import com.Projects.AirBnbApp.entity.enums.BookingStatus;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.exception.UnAuthorisedException;
import com.Projects.AirBnbApp.respository.*;
import com.Projects.AirBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;


    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public String initiateBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));
        User user=getCurrUser();
        if (!java.util.Objects.equals(
                user.getId(),
                booking.getUser().getId())) {

            throw new UnAuthorisedException(
                    "Booking does not belong to this user with id: " + user.getId()
            );
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl=checkoutService.getCheckoutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/failure");
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING );
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {

        if (!"checkout.session.completed".equals(event.getType())) {
            log.info("Ignoring unsupported Stripe event type: {}", event.getType());
            return;
        }

        Session session = event.getDataObjectDeserializer()
                .getObject()
                .filter(Session.class::isInstance)
                .map(Session.class::cast)
                .orElse(null);

        if (session == null) {
            log.error("Could not deserialize Checkout Session for event: {}", event.getId());
            throw new IllegalStateException(
                    "Could not deserialize Stripe Checkout Session"
            );
        }

        String sessionId = session.getId();

        log.info(
                "Processing Checkout Session: {}, payment status: {}",
                sessionId,
                session.getPaymentStatus()
        );

        // Never confirm a booking unless Stripe says the payment is paid.
        if (!"paid".equals(session.getPaymentStatus())) {
            log.warn(
                    "Checkout Session {} is not paid. Current status: {}",
                    sessionId,
                    session.getPaymentStatus()
            );
            return;
        }

        Booking booking = bookingRepository.findByPaymentSessionId(sessionId)
                .orElse(null);

        if (booking == null) {
            // Do not confirm an unrelated booking.
            // Investigate/reconcile this event if it represents a real payment.
            log.error(
                    "No booking matches paid Stripe Checkout Session {}. Event ID: {}",
                    sessionId,
                    event.getId()
            );
            return;
        }

        // Prevent duplicate webhook delivery from confirming inventory twice.
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            log.info("Booking {} is already confirmed; ignoring duplicate event", booking.getId());
            return;
        }

        if (booking.getBookingStatus() != BookingStatus.PAYMENT_PENDING) {
            log.warn(
                    "Booking {} has unexpected status {}. Not confirming it.",
                    booking.getId(),
                    booking.getBookingStatus()
            );
            return;
        }

        var roomId = booking.getRoom().getId();
        var checkIn = booking.getCheckInDate();
        var checkOut = booking.getCheckOutDate();
        var roomsCount = booking.getRoomsCount();

        var lockedInventory = inventoryRepository.findAndLockReservedInventory(
                roomId, checkIn, checkOut, roomsCount
        );


        long expectedDays = ChronoUnit.DAYS.between(checkIn, checkOut)+1;

        log.info("Booking {}: locked inventory rows={}, expected={}, roomId={}, {} -> {}, roomsCount={}",
                booking.getId(), lockedInventory.size(), expectedDays, roomId, checkIn, checkOut, roomsCount);

        if (lockedInventory.size() != expectedDays) {
            throw new IllegalStateException(
                    "Inventory mismatch for booking " + booking.getId()
            );
        }
        log.info("Processing Stripe event type: {}", event.getType());

        log.info("Checkout session ID: {}", session.getId());

        log.info("Checkout payment status: {}", session.getPaymentStatus());

        log.info("Looking up booking by payment session ID: {}", session.getId());

        inventoryRepository.confirmBooking(
                roomId, checkIn, checkOut, roomsCount
        );

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        log.info(
                "Successfully confirmed booking {} for Stripe session {}",
                booking.getId(),
                sessionId
        );
    }

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("Initializing booking for hotel: {}, room: {},date {} - {}",bookingRequest.getHotelId(),bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());
        Hotel hotel=hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id: "+bookingRequest.getHotelId()));
        Room room=roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(()->
                new ResourceNotFoundException("Room not found with id: "+bookingRequest.getRoomId()));
        List<Inventory> inventoryList=inventoryRepository.findAllLockAvailableInventory(room.getId()
                ,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());
        long daysCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size()!=daysCount){
            throw  new IllegalStateException("Room is not available anymore");
        }

        // Reserve the room/ update the booked count of inventories

        inventoryRepository.initBooking(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        // create the booking
        BigDecimal priceOfOneRoom=pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice=priceOfOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));
        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrUser())
                .amount(totalPrice)
                .roomsCount(bookingRequest.getRoomsCount())
                .build();

        booking=bookingRepository.save(booking);
        return modelMapper.map(booking,BookingDto.class);
    }

    @Transactional
    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests with booking id :{}",bookingId);
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));
        User user=getCurrUser();
//        log.info("Current User id :{} and Booking user id : {}",user.getId(),booking.getUser().getId());
        log.info("Current user class: {}", user.getClass().getName());
        log.info("Booking user class: {}", booking.getUser().getClass().getName());

        log.info("Current user ID: {}", user.getId());
        log.info("Booking user ID: {}", booking.getUser().getId());

        log.info("Entity equals result: {}", user.equals(booking.getUser()));
        log.info("ID equals result: {}",
                java.util.Objects.equals(
                        user.getId(),
                        booking.getUser().getId()
                ));
        if (!java.util.Objects.equals(
                user.getId(),
                booking.getUser().getId())) {

            throw new UnAuthorisedException(
                    "Booking does not belong to this user with id: " + user.getId()
            );
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }
        for(GuestDto guestDto:guestDtoList){
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(getCurrUser());
            guest= guestRepository.save(guest);

        }
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));
        User user=getCurrUser();
        if (!java.util.Objects.equals(
                user.getId(),
                booking.getUser().getId())) {

            throw new UnAuthorisedException(
                    "Booking does not belong to this user with id: " + user.getId()
            );
        }
        if(booking.getBookingStatus()!=BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only confirmed booking can be canceled");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getRoomsCount());
        inventoryRepository.cancelBooking(booking.getRoom().getId(),booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getRoomsCount());
        // handle the refund

        try {
            Session session=Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams=RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));
        User user=getCurrUser();
        if (!java.util.Objects.equals(
                user.getId(),
                booking.getUser().getId())) {

            throw new UnAuthorisedException(
                    "Booking does not belong to this user with id: " + user.getId()
            );
        }
        return booking.getBookingStatus().name();
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
    public User getCurrUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
