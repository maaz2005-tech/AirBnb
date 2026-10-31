package com.Projects.AirBnbApp.controller;


import com.Projects.AirBnbApp.dto.BookingDto;
import com.Projects.AirBnbApp.dto.GuestDto;
import com.Projects.AirBnbApp.dto.ProfileUpdateRequestDto;
import com.Projects.AirBnbApp.dto.UserDto;
import com.Projects.AirBnbApp.entity.Guest;
import com.Projects.AirBnbApp.service.BookingService;
import com.Projects.AirBnbApp.service.GuestService;
import com.Projects.AirBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto){
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getMyBooking(){
        return ResponseEntity.ok(bookingService.getMyBookings());
    }
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(){
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/guests")
    public ResponseEntity<List<GuestDto>> getAllGuests(){
        return ResponseEntity.ok(guestService.findAllGuests());
    }@GetMapping("/guests/{guestId}")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long guestId){
        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }
    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<Void> deleteGuestById(@PathVariable Long guestId){
        guestService.deleteById(guestId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/guests")
    public ResponseEntity<GuestDto> createGuest(@RequestBody GuestDto guestDto){
        return new ResponseEntity<>(guestService.createNewGuest(guestDto),HttpStatus.CREATED);
    }
    @PatchMapping("/guests/{guestId}")
    public ResponseEntity<GuestDto> updateGuestById(@PathVariable Long guestId,@RequestBody GuestDto guestDto){
        return new ResponseEntity<>(guestService.updateGuest(guestId,guestDto),HttpStatus.OK);
    }
}
