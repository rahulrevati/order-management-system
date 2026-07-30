package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.Requests.LoginRequest;
import com.ecommerce.auth.dto.Responses.LoginResponse;
import com.ecommerce.auth.dto.Requests.RegisterRequest;
import com.ecommerce.auth.dto.Responses.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);
}
