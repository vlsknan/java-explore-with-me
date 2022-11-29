package ru.practicum.exception.model;

public class CompilationNotFoundException extends NotFoundException {
    public CompilationNotFoundException(int id) {
        super("Compilation with id=" + id + " was not found.");
    }
}
