package ru.practicum.exception.model;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(int id) {
        super("Category with id=" + id + " was not found.");
    }
}
