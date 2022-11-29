package ru.practicum.exception.model;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException(int id) {
        super("Comment with id=" + id + " was not found.");
    }
}
