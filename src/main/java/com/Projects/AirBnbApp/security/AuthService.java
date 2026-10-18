package com.Projects.AirBnbApp.security;


import com.Projects.AirBnbApp.dto.LoginDto;
import com.Projects.AirBnbApp.dto.SignUpRequestDto;
import com.Projects.AirBnbApp.dto.UserDto;
import com.Projects.AirBnbApp.entity.User;
import com.Projects.AirBnbApp.entity.enums.Role;
import com.Projects.AirBnbApp.exception.ResourceNotFoundException;
import com.Projects.AirBnbApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public UserDto signup(SignUpRequestDto signUpRequestDto){
        User user=userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if(user!=null){
            throw new RuntimeException("User already exists with email: "+signUpRequestDto.getEmail());
        }
        User newUser= modelMapper.map(signUpRequestDto,User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser=userRepository.save(newUser);
        return modelMapper.map(newUser,UserDto.class);
    }
    public String[] login(LoginDto loginDto){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()
        ));
        User user=(User) authentication.getPrincipal();
        return new String[]{jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user)};
    }
    public String refreshToken(String token){
        Long id= jwtService.getUserIdFromToken(token);
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+id));
        return jwtService.generateAccessToken(user);
    }
}
