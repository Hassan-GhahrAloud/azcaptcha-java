package com.azcaptcha.exceptions;

public class TimeoutException extends Exception {

    public TimeoutException(String errorMessage) {
        super(errorMessage);
    }

}
