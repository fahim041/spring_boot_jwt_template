package com.example.jwt_auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserDto {
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long")
    private String password;
}
