package ru.practicum.comment.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.admin.service.CommentAdminService;
import ru.practicum.comment.model.dto.CommentDtoOut;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events/{eventId}/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Validated
public class CommentAdminController {
    final CommentAdminService service;

    //Получить все комментарии к событию
    @GetMapping
    public List<CommentDtoOut> getCommentsByEvent(@PathVariable @Positive int eventId,
                                                  @RequestParam(required = false) List<Integer> users,
                                                  @RequestParam(required = false) List<String> status,
                                                  @RequestParam(required = false) String start,
                                                  @RequestParam(required = false) String end,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получить все комментарии к событию с id = {} (CommentAdminController)", eventId);
        return service.getAllCommentsByEvent(eventId, users, status, start, end, from, size);
    }

    //Опубликовать комментарий
    @PatchMapping("/{comId}/publish")
    public CommentDtoOut publishComment(@PathVariable @Positive int eventId, @PathVariable @Positive int comId) {
        log.info("Оппубликовать комментарий с id = {} (CommentAdminController)", comId);
        return service.publishComment(eventId, comId);
    }

    //Отклонить комментарий
    @PatchMapping("/{comId}/reject")
    public CommentDtoOut rejectComment(@PathVariable @Positive int eventId, @PathVariable @Positive int comId) {
        log.info("Отклонить комментарий комментарий с id = {} (CommentAdminController)", comId);
        return service.rejectComment(eventId, comId);
    }
}
