package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.HotelDto;
import com.Projects.AirBnbApp.dto.RoomDto;
import com.Projects.AirBnbApp.entity.Hotel;
import com.Projects.AirBnbApp.entity.Room;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.respository.HotelRepository;
import com.Projects.AirBnbApp.respository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoomServiceImpl implements RoomService{
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new room in hotel with Id: {}",hotelId);
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: "+hotelId));
        Room room=modelMapper.map(roomDto,Room.class);
        room.setHotel(hotel);
        room= roomRepository.save(room);
        //create inventory as soon as room is created and if hotel is active
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with Id: {}",hotelId);
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: "+hotelId));

        return hotel.getRooms()
                .stream()
                .map(element->modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long id) {
        return modelMapper.map(roomRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Room not found with Id:"+id)),RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long id) {
        log.info("Deleting the room with Id: {}",id);
        Room room= roomRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Room not found with Id:"+id));
        // delete all future inventory for this room
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(id);
    }
}
