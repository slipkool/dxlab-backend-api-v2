package com.dxlab.dxlabbackendapi.domain.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
