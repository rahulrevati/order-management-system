package com.ecommerce.auth.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String firstName;
    private String lastName;
    private Long userId;
    private String email;
    private String role;
    private String accessToken;
    private String tokenType;

}
