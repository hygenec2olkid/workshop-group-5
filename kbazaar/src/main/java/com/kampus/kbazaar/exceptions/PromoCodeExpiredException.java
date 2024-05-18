package com.kampus.kbazaar.exceptions;

public class PromoCodeExpiredException extends RuntimeException {
    public PromoCodeExpiredException(String message) {
        super(message);
    }
}
