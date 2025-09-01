package com.example.jwt_auth.controllers;

import com.example.jwt_auth.config.JwtConfig;
import com.example.jwt_auth.dtos.JwtResponseDto;
import com.example.jwt_auth.dtos.LoginRequestDto;
import com.example.jwt_auth.dtos.UserDto;
import com.example.jwt_auth.mappers.UserMapper;
import com.example.jwt_auth.repositories.UserRepository;
import com.example.jwt_auth.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserMapper  userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
            ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        var user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);

        response.addCookie(cookie);
        return ResponseEntity.ok(new JwtResponseDto(accessToken.toString()));
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDto> currentUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long)authentication.getPrincipal();
        var user = userRepository.findById(userId).orElseThrow();
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ){
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || !jwt.isValid()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return  ResponseEntity.ok(new JwtResponseDto(accessToken.toString()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
