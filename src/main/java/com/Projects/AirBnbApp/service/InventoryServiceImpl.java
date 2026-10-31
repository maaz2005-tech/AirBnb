package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.*;
import com.Projects.AirBnbApp.entity.*;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.respository.HotelMinPriceRepository;
import com.Projects.AirBnbApp.respository.InventoryRepository;
import com.Projects.AirBnbApp.respository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.Projects.AirBnbApp.util.AppUtils.getCurrUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotel for {} form {} to {}",hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate());
        Pageable pageable= PageRequest.of(hotelSearchRequest.getPage(),hotelSearchRequest.getSize());
        long dateCount= ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;

        // business logic - 90 days
        Page<HotelPriceDto> hotelPage=hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),dateCount,pageable);
//
      return hotelPage;
    }

    @Override
    public void deleteAllInventories(Room room) {
//        LocalDate today=LocalDate.now();
        log.info("Deleting the inventories of room with id: {}",room.getId());
        inventoryRepository.deleteByRoom(room);
    }
    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);
        for(;!today.isAfter(endDate);today=today.plusDays(1)){
            Inventory inventory=Inventory
                    .builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);

        }
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting all the inventory for room with ID: {}",roomId);
        Room room=roomRepository.findById(roomId).orElseThrow(
                ()->new ResourceNotFoundException("Room not found with ID: "+roomId)
        );
        User user=getCurrUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You are not the owner of the Room with ID: "+roomId);
        }

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map((element)->modelMapper.map(element, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Updating all the inventory for room with ID: {} between date range: {} - {}",
                roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate());

        Room room=roomRepository.findById(roomId).orElseThrow(
                ()->new ResourceNotFoundException("Room not found with ID: "+roomId)
        );
        User user=getCurrUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You are not the owner of the Room with ID: "+roomId);
        }
        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate(),updateInventoryRequestDto.getSurgeFactor(),updateInventoryRequestDto.getClosed());
        inventoryRepository.updateInventory(roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate(),updateInventoryRequestDto.getSurgeFactor(),updateInventoryRequestDto.getClosed());
    }
}
