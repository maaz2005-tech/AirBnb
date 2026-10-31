package com.Projects.AirBnbApp.service;

import com.Projects.AirBnbApp.dto.ProfileUpdateRequestDto;
import com.Projects.AirBnbApp.dto.UserDto;
import com.Projects.AirBnbApp.entity.User;
import org.jspecify.annotations.Nullable;

public interface UserService{
    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
