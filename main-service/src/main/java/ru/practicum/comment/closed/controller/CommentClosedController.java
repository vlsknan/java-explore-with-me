package ru.practicum.comment.closed.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.closed.service.CommentClosedService;
import ru.practicum.comment.model.dto.CommentDtoIn;
import ru.practicum.comment.model.dto.CommentDtoOut;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("users/{userId}")
@Slf4j
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentClosedController {
    final CommentClosedService service;

    //Получить все комментарии пользователя с фильтрацией
    @GetMapping("/comments")
    public List<CommentDtoOut> getAllCommentsByUser(@PathVariable @Positive int userId,
                                                    @RequestParam(required = false) List<Integer> events,
                                                    @RequestParam(required = false) List<String> status,
                                                    @RequestParam(required = false) String start,
                                                    @RequestParam(required = false) String end,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получить все комментарии пользователя с id = {} (CommentClosedController)", userId);
        return service.getAllCommentsByUser(userId, events, status, start, end, from, size);
    }

    //Получить комментарий по id
    @GetMapping("/comments/{comId}")
    public CommentDtoOut getCommentByUserAndId(@PathVariable @Positive int userId, @PathVariable @Positive int comId) {
        log.info("Получить комментарий с id = {} (CommentClosedController)", comId);
        return service.getCommentById(userId, comId);
    }

    //Добавить комментарий пользователя к событию
    @PostMapping("events/{eventId}/comments")
    public CommentDtoOut createComment(@PathVariable @Positive int userId, @PathVariable @Positive int eventId,
                                       @RequestBody @Valid CommentDtoIn commentDto) {
        log.info("Добавить к событию с id = {} комментарий пользователя с id = {} (CommentClosedController)",
                eventId, userId);
        return service.createComment(userId, eventId, commentDto);
    }

    //Редактировать комментарий к событию (с момента публикации прошло не более 2 дней)
    @PatchMapping("/events/{eventId}/comments/{comId}")
    public CommentDtoOut updateComment(@PathVariable @Positive int userId, @PathVariable @Positive int eventId,
                                       @PathVariable @Positive int comId, @RequestBody @Valid CommentDtoIn commentDto) {
        log.info("Редактировать комментарий с id = {} (CommentClosedController)", comId);
        return service.updateComment(userId, eventId, comId, commentDto);
    }

    //Удалить комментарий по id
    @DeleteMapping("/comments/{comId}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable @Positive int userId,
                                                    @PathVariable @Positive int comId) {
        log.info("Удалить комментарий с id = {} (CommentClosedController)", comId);
        service.deleteComment(userId, comId);
        return ResponseEntity.ok().build();
    }
}
