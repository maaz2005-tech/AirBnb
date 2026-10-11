package com.Projects.AirBnbApp.respository;

import com.Projects.AirBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {

}
