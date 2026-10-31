package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.HotelDto;
import com.Projects.AirBnbApp.dto.HotelInfoDto;
import com.Projects.AirBnbApp.dto.RoomDto;
import com.Projects.AirBnbApp.entity.Hotel;
import com.Projects.AirBnbApp.entity.Room;
import com.Projects.AirBnbApp.entity.User;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.exception.UnAuthorisedException;
import com.Projects.AirBnbApp.respository.HotelRepository;
import com.Projects.AirBnbApp.respository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.asm.IModelFilter;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.Projects.AirBnbApp.util.AppUtils.getCurrUser;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private  final InventoryService inventoryService;

    @Override
    public HotelInfoDto getInfoById(Long hotelId) {
        Hotel hotel=hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found wiht Id: "+id));
        List<RoomDto> rooms=hotel.getRooms().stream()
                .map((e)->modelMapper.map(e,RoomDto.class))
                .collect(Collectors.toList());
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}",hotelDto.getName());
        Hotel hotel=modelMapper.map(hotelDto,Hotel.class);
        hotel.setActive(false);
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);
        Hotel savedHotel=hotelRepository.save(hotel);
        return modelMapper.map(savedHotel,HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found wiht Id: "+id));
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("The user does not own this hotel with Id: "+user.getId());
        }
        // delete the future inventories for this hotel
        for(Room room :hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
        log.info("Successfully deleted the hotel with ID: {}",id);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with Id: {}",id);
        Hotel hotel=hotelRepository
            .findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Hotel not found wiht Id: "+id));

        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("The user does not own this hotel with Id: "+user.getId());
        }

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating the hotel with Id: {}",id);
        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: {}"+id));
        hotel.setActive(true);
        // create inventory for all the rooms for this hotel
        // assuming only do it once
        for(Room room :hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Getting the hotel with ID: {}",id);
        Hotel hotel=hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with Id: "+id));
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("The user does not own this hotel with Id: "+user.getId());
        }
        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
        hotel=hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }
    @Override
    public List<HotelDto> getAllHotels() {
        User user=getCurrUser();
        log.info("Getting all hotels for the admin user with Id: {}",user.getId());
        return hotelRepository.findAllByOwner(user).stream()
                .map((hotel)->modelMapper.map(hotel,HotelDto.class))
                .collect(Collectors.toList());
    }
    //
}
