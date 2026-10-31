package com.Projects.AirBnbApp.dto;


import com.Projects.AirBnbApp.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private LocalDate dateOfBirth;
}
