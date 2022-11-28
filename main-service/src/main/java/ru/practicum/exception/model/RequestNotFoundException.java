package ru.practicum.exception.model;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(int id) {
        super("Request with id=" + id + " was not found.");
    }
}
