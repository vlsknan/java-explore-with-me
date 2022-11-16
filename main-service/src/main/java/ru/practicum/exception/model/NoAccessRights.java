package ru.practicum.exception.model;

//403
public class NoAccessRights extends RuntimeException {
    public NoAccessRights(String message) {
        super(message);
    }
}
