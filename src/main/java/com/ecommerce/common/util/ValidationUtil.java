package com.ecommerce.common.util;

import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    public static boolean isValidEmail(String email) {
        boolean isValid = email != null && EMAIL_PATTERN.matcher(email).matches();
        log.debug("Email validation for {}: {}", email, isValid);
        return isValid;
    }

    public static boolean isValidPhone(String phone) {
        boolean isValid = phone != null && PHONE_PATTERN.matcher(phone).matches();
        log.debug("Phone validation for {}: {}", phone, isValid);
        return isValid;
    }
}
