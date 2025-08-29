package com.example.jwt_auth.mappers;

import com.example.jwt_auth.dtos.RegisterUserDto;
import com.example.jwt_auth.dtos.UserDto;
import com.example.jwt_auth.entites.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserDto registerUserDto);
}
