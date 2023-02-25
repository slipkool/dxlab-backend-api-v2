package com.dxlab.dxlabbackendapi.domain.exception;

public class LaboratoryResultException extends RuntimeException{
    public LaboratoryResultException(String message) {
        super(message);
    }

    public LaboratoryResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
