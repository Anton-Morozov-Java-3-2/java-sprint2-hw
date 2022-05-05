package com.practikum.tracker.manager;

public class ManagerValidateException extends RuntimeException{
    public ManagerValidateException(final String message) {
        super(message);
    }
    public ManagerValidateException() {}
}
