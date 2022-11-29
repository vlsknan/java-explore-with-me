package ru.practicum.comment.common.service;

import ru.practicum.comment.model.dto.CommentDtoOut;

import java.util.List;

public interface CommentCommonService {
    List<CommentDtoOut> getAll(int eventId, int from, int size);

    CommentDtoOut getCommentById(int eventId, int comId);
}
