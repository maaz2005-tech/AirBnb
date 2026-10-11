package com.Projects.AirBnbApp.dto;

import com.Projects.AirBnbApp.entity.Booking;
import com.Projects.AirBnbApp.entity.User;
import com.Projects.AirBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
//    private Set<BookingDto> bookings;
}
