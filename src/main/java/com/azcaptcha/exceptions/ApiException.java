package com.azcaptcha.exceptions;

public class ApiException extends Exception {

    public ApiException(String errorMessage) {
        super(errorMessage);
    }

}
