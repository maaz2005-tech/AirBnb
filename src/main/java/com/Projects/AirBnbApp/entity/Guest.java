package com.Projects.AirBnbApp.entity;


import com.Projects.AirBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;

import java.util.Set;

@Entity
@Getter
@Setter

public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Integer age;

    @ManyToMany(mappedBy = "guests")
    private Set<Booking> bookings;
}
