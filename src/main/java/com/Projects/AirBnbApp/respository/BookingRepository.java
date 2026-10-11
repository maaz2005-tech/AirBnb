package com.Projects.AirBnbApp.respository;


import com.Projects.AirBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {

}
