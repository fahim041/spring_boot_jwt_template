package com.example.jwt_auth.controllers;

import com.example.jwt_auth.config.JwtConfig;
import com.example.jwt_auth.dtos.JwtResponseDto;
import com.example.jwt_auth.dtos.LoginRequestDto;
import com.example.jwt_auth.repositories.UserRepository;
import com.example.jwt_auth.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

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
}
