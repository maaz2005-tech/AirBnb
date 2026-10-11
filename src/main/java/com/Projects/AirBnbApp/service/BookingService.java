package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.BookingDto;
import com.Projects.AirBnbApp.dto.BookingRequest;
import com.Projects.AirBnbApp.dto.GuestDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
