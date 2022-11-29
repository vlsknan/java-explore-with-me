package ru.practicum.exception.model;

//403
public class ConditionsNotMet extends RuntimeException {
    public ConditionsNotMet(String message) {
        super(message);
    }
}
