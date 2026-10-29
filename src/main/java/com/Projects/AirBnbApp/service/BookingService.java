package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.BookingDto;
import com.Projects.AirBnbApp.dto.BookingRequest;
import com.Projects.AirBnbApp.dto.GuestDto;
import com.stripe.model.Event;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiateBooking(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);
}
