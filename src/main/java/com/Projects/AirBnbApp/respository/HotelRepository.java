package com.Projects.AirBnbApp.respository;

import com.Projects.AirBnbApp.entity.Hotel;
import com.Projects.AirBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findAllByOwner(User user);
}
