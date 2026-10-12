package com.Projects.AirBnbApp.controller;

import com.Projects.AirBnbApp.dto.HotelDto;
import com.Projects.AirBnbApp.dto.HotelInfoDto;
import com.Projects.AirBnbApp.dto.HotelPriceDto;
import com.Projects.AirBnbApp.dto.HotelSearchRequest;
import com.Projects.AirBnbApp.service.HotelService;
import com.Projects.AirBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {


    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        var page=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getInfoById(hotelId));
    }
}
