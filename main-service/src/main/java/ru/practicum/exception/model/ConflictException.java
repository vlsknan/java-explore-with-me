package ru.practicum.exception.model;
//409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
