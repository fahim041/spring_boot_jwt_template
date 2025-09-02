package com.example.jwt_auth.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisRefreshToken {
    private String refreshToken;
    private boolean valid;
}
