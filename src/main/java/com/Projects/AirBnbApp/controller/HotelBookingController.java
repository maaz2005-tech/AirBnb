package com.Projects.AirBnbApp.controller;

import com.Projects.AirBnbApp.dto.BookingDto;
import com.Projects.AirBnbApp.dto.BookingRequest;
import com.Projects.AirBnbApp.dto.GuestDto;
import com.Projects.AirBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {
    private final BookingService bookingService;
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDtoList));
    }
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String,String>> initiatePayments(@PathVariable Long bookingId){
        String sessionUrl=bookingService.initiateBooking(bookingId);
        return ResponseEntity.of(Optional.of(Map.of("sessionUrl", sessionUrl)));
    }
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Map<String,String>> cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String,String>> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(Map.of("Status: ",bookingService.getBookingStatus(bookingId)));
    }
}
