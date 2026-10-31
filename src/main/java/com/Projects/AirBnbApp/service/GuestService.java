package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.GuestDto;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface GuestService {

    List<GuestDto> findAllGuests();

    void deleteById(Long guestId);

    GuestDto findGuestById(Long guestId);
    GuestDto createNewGuest(GuestDto guestDto);
    GuestDto updateGuest(Long guestId,GuestDto guestDto);
}
