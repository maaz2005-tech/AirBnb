package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.HotelDto;
import com.Projects.AirBnbApp.dto.HotelInfoDto;
import com.Projects.AirBnbApp.entity.Hotel;
import org.jspecify.annotations.Nullable;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id,HotelDto hotelDto);
    void deleteHotelById(Long id);
    void activateHotel(Long id);

    HotelInfoDto getInfoById(Long hotelId);
}
