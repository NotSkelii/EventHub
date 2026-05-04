package com.skeli.eventservice.exception;

public class DuplicateExceptionHandler extends RuntimeException{
    public DuplicateExceptionHandler(String message) {
        super(message);
    }
}
