package com.Projects.AirBnbApp.respository;


import com.Projects.AirBnbApp.entity.Booking;
import com.Projects.AirBnbApp.entity.Hotel;
import com.Projects.AirBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);
    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDateTime,LocalDateTime endDateTime);

    List<Booking> findByUser(User user);
}
