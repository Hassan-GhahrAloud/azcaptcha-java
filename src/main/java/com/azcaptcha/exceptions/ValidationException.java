package com.azcaptcha.exceptions;

public class ValidationException extends Exception {

    public ValidationException(String errorMessage) {
        super(errorMessage);
    }

}
