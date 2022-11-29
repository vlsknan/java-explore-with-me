package ru.practicum.comment.admin.service;

import ru.practicum.comment.model.dto.CommentDtoOut;

import java.util.List;

public interface CommentAdminService {
    List<CommentDtoOut> getAllCommentsByEvent(int eventId, List<Integer> users, List<String> status,
                                              String start, String end, int from, int size);

    CommentDtoOut publishComment(int eventId, int comId);

    CommentDtoOut rejectComment(int eventId, int comId);
}
