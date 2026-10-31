package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.GuestDto;
import com.Projects.AirBnbApp.entity.Guest;
import com.Projects.AirBnbApp.entity.User;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.respository.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.Projects.AirBnbApp.util.AppUtils.getCurrUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService{
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDto> findAllGuests() {
        return guestRepository.findAllByUser(getCurrUser()).stream()
                .map((ele)->modelMapper.map(ele,GuestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public GuestDto findGuestById(Long guestId) {

        return modelMapper.map(guestRepository.findById(guestId),GuestDto.class);
    }

    @Override
    public void deleteById(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));
        User user = getCurrUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");
        guestRepository.deleteById(guestId);
    }

    @Override
    public GuestDto createNewGuest(GuestDto guestDto) {
        Guest savedGuest=guestRepository.save(modelMapper.map(guestDto,Guest.class));
        savedGuest.setUser(getCurrUser());
        return modelMapper.map(savedGuest,GuestDto.class);
    }

    @Override
    public GuestDto updateGuest(Long guestId, GuestDto guestDto) {
        Guest guest=guestRepository.findById(guestId).orElseThrow(
                ()-> new ResourceNotFoundException("Guest not found with ID: "+guestId));

        User currentUser = getCurrUser();
        if(!currentUser.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        modelMapper.map(guestDto,guest);
        guest.setUser(currentUser);
        guest.setId(guestId);
        guestRepository.save(guest);
        return modelMapper.map(guest,GuestDto.class);
    }
}
