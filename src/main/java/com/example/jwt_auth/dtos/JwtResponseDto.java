package com.example.jwt_auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtResponseDto {
    private String token;
}
