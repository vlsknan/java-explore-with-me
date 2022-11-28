package ru.practicum.exception.model;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(int id) {
        super("User with id=" + id + " was not found.");
    }
}
