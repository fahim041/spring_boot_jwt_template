package com.example.jwt_auth.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
}
