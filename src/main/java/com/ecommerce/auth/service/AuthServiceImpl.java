package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.Requests.LoginRequest;
import com.ecommerce.auth.dto.Responses.LoginResponse;
import com.ecommerce.auth.dto.Requests.RegisterRequest;
import com.ecommerce.auth.dto.Responses.RegisterResponse;
import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.mapper.UserMapper;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.common.constants.SecurityConstants;
import com.ecommerce.common.enums.RoleName;
import com.ecommerce.common.exception.ResourceAlreadyExistsException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.security.jwt.JwtService;
import com.ecommerce.common.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;



    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", registerRequest.getEmail());
            throw new ResourceAlreadyExistsException("Email already exists.");
        }
        if(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())){
            log.warn("Registration failed: Phone number already exists - {}", registerRequest.getPhoneNumber());
            throw new ResourceAlreadyExistsException("Phone number already exists.");
        }
        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER).orElseThrow(() -> new ResourceNotFoundException("Default role CUSTOMER not found"));
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(customerRole);
        User savedUser=userRepository.save(user);
        log.info("User registered successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
        return userMapper.toRegisterResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        String jwtToken = jwtService.generateToken(userDetails);
        log.info("Login successful for user with ID: {} and email: {}", user.getId(), user.getEmail());
        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName().name())
                .accessToken(jwtToken)
                .tokenType(SecurityConstants.TOKEN_TYPE)
                .build();
    }
}
