package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.ProfileUpdateRequestDto;
import com.Projects.AirBnbApp.dto.UserDto;
import com.Projects.AirBnbApp.entity.User;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.Projects.AirBnbApp.util.AppUtils.getCurrUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private  final ModelMapper modelMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User id not found with Id: "+id));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user=getCurrUser();
        if(profileUpdateRequestDto.getDateOfBirth()!=null){
            user.setDateOfBirth(profileUpdateRequestDto.getDateOfBirth());
        }
        if(profileUpdateRequestDto.getName()!=null){
            user.setName(profileUpdateRequestDto.getName());
        }
        if(profileUpdateRequestDto.getGender()!=null) user.setGender(profileUpdateRequestDto.getGender());
        userRepository.save(user);

    }

    @Override
    public UserDto getMyProfile() {
        log.info("Getting the profile for user who is logged in");
        return modelMapper.map(getCurrUser(),UserDto.class);
    }
}
