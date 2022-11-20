package ru.practicum.user.closed.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.model.dto.RequestDto;
import ru.practicum.user.closed.service.UserClosedService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class UserClosedController {
    final UserClosedService service;

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping
    public List<RequestDto> findRequestByRequesterId(@Positive @PathVariable int userId) {
        return service.findByRequesterId(userId);
    }

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping
    public RequestDto addRequestFromUser(@Positive @PathVariable int userId, @Positive @RequestParam int eventId) {
        return service.addRequest(userId, eventId);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@Positive @PathVariable int userId, @Positive @PathVariable int requestId) {
        return service.cancelRequest(userId, requestId);
    }
}
