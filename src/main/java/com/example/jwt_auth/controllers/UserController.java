package com.example.jwt_auth.controllers;

import com.example.jwt_auth.dtos.RegisterUserDto;
import com.example.jwt_auth.dtos.UserDto;
import com.example.jwt_auth.entites.Role;
import com.example.jwt_auth.mappers.UserMapper;
import com.example.jwt_auth.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository  userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserDto> getAllUsers(){
        var users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterUserDto registerUserDto){
        if(userRepository.existsByEmail(registerUserDto.getEmail())){
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is already taken!")
            );
        }

        var user = userMapper.toEntity(registerUserDto);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }
}
