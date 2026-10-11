package com.Projects.AirBnbApp.respository;

import com.Projects.AirBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}