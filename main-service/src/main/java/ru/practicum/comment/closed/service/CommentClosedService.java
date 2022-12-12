package ru.practicum.comment.closed.service;

import ru.practicum.comment.model.dto.CommentDtoIn;
import ru.practicum.comment.model.dto.CommentDtoOut;

import java.util.List;

public interface CommentClosedService {
    List<CommentDtoOut> getAllCommentsByUser(int userId, List<Integer> events, List<String> status,
                                             String start, String end, int from, int size);

    CommentDtoOut getCommentById(int userId, int comId);

    CommentDtoOut createComment(int userId, int eventId, CommentDtoIn commentDto);

    CommentDtoOut updateComment(int userId, int eventId, int comId, CommentDtoIn commentDto);

    void deleteComment(int userId, int comId);
}
