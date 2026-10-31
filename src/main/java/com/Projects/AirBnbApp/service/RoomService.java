package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.RoomDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomById(Long id);
    void deleteRoomById(Long id);

    RoomDto updateRoomByid(Long hotelId,Long roomId, RoomDto roomDto);
}
