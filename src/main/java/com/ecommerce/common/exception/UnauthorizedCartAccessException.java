package com.ecommerce.common.exception;

public class UnauthorizedCartAccessException extends RuntimeException {

    public UnauthorizedCartAccessException(String message) {
        super(message);
    }
}
