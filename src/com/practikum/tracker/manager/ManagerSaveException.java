package com.practikum.tracker.manager;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(final String message) {
        super(message);
    }
    public ManagerSaveException() {}
}
