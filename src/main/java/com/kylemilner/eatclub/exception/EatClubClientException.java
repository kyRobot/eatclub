package com.kylemilner.eatclub.exception;

public class EatClubClientException extends RuntimeException {
    public EatClubClientException(String message) {
        super(message);
    }

    public EatClubClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
