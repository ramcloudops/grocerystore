package com.turmericstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String userId;
    private String email;
    private String fullName;
    private Set<String> roles;
    private Long expiresIn;
    private String tokenType = "Bearer";
}
