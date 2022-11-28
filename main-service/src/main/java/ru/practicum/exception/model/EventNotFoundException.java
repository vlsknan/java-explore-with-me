package ru.practicum.exception.model;

public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException(int id) {
        super("Event with id=" + id + " was not found");
    }
}
