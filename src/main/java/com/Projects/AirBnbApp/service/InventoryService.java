package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.*;
import com.Projects.AirBnbApp.entity.Room;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
