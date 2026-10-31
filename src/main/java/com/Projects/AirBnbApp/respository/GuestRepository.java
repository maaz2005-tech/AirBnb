package com.Projects.AirBnbApp.respository;

import com.Projects.AirBnbApp.entity.Guest;
import com.Projects.AirBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findAllByUser(User currUser);
}