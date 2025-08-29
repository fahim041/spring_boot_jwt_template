package com.example.jwt_auth.dtos;

import com.example.jwt_auth.entites.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private Role role;
}
