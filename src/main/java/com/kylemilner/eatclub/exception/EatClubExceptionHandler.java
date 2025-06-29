package com.kylemilner.eatclub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class EatClubExceptionHandler {
    @ExceptionHandler(EatClubClientException.class)
    @ResponseBody
    public ResponseEntity<String> handleEatClubClientException(EatClubClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }
}
