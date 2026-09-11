package com.ecommerce.common.security.config;


import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.common.enums.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.enabled:false}")
    private boolean bootstrapAdminEnabled;

    @Value("${app.bootstrap-admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseGet(() -> {
                    log.info("Creating ADMIN role");

                    return roleRepository.save(
                            Role.builder()
                                    .roleName(RoleName.ADMIN)
                                    .build());
                });

        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
                .orElseGet(() -> {
                    log.info("Creating CUSTOMER role");

                    return roleRepository.save(
                            Role.builder()
                                    .roleName(RoleName.CUSTOMER)
                                    .build());
                });
        if (!bootstrapAdminEnabled) {
            log.info("Bootstrap admin creation is disabled");
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("Bootstrap admin is enabled but APP_BOOTSTRAP_ADMIN_PASSWORD is not set");
        }

        if (!userRepository.existsByEmail(adminEmail)) {

            log.info("Creating default admin user");

            User admin = User.builder()
                    .firstName("System")
                    .lastName("Administrator")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .phoneNumber("9999999999")
                    .enabled(true)
                    .role(adminRole)
                    .build();

            userRepository.save(admin);

            log.info("Default admin user created successfully.");
        }
    }
}