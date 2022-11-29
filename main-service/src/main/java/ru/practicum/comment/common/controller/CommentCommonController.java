package ru.practicum.comment.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.common.service.CommentCommonService;
import ru.practicum.comment.model.dto.CommentDtoOut;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class CommentCommonController {
    final CommentCommonService service;

    //Получение всех комментариев к событию
    @GetMapping
    public List<CommentDtoOut> getAllComments(@PathVariable @Positive int eventId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получить все комментарии к событию с id = {} (CommentCommonController)", eventId);
        return service.getAll(eventId, from, size);
    }

    //Получить информацию о комментарии по id
    @GetMapping("/{comId}")
    public CommentDtoOut getCommentById(@PathVariable int eventId, @PathVariable int comId) {
        log.info("Получить комментарий с id = {} (CommentCommonController)", comId);
        return service.getCommentById(eventId, comId);
    }
}
