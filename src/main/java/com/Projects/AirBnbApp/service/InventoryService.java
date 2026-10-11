package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.HotelDto;
import com.Projects.AirBnbApp.dto.HotelSearchRequest;
import com.Projects.AirBnbApp.dto.RoomDto;
import com.Projects.AirBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
