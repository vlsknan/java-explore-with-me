package ru.practicum.event.closed.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.closed.service.EventClosedService;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.dto.NewEventInDto;
import ru.practicum.event.model.dto.UpdateEventRequest;
import ru.practicum.request.model.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Validated
public class EventClosedController {
    final EventClosedService service;

    //Получение событий, добавленных текущим пользователем
    @GetMapping
    public List<EventShortOutDto> findEventByUser(@PathVariable(required = false) @Positive int userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return service.findEventByUser(userId, from, size);
    }

    //Изменение события добавленного текущим пользователем
    @PatchMapping
    public EventFullOutDto updateEvent(@PathVariable(required = false) @Positive int userId,
                                       @RequestBody(required = false) @Valid UpdateEventRequest updateRequest) {
        return service.update(userId, updateRequest);
    }

    //Добавление нового события
    @PostMapping
    public EventFullOutDto createEvent(@PathVariable(required = false) @Positive int userId,
                                       @RequestBody(required = false) @Valid NewEventInDto newEvent) {
        return service.create(userId, newEvent);
    }

    //Получение полной информации о событии добавленном текущим пользователем
    @GetMapping("/{eventId}")
    public EventFullOutDto findEventById(@PathVariable(required = false) @Positive int userId,
                                         @PathVariable(required = false) @Positive int eventId) {
        return service.findEventById(userId, eventId);
    }

    //Отмена события добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    public EventFullOutDto cancelEvent(@PathVariable(required = false) @Positive int userId,
                                           @PathVariable(required = false) @Positive int eventId) {
        return service.cancelEvent(userId, eventId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{eventId}/requests")
    public List<RequestDto> findRequestsByUser(@PathVariable(required = false) @Positive int userId,
                                               @PathVariable(required = false) @Positive int eventId) {
        return service.findRequestsByUser(userId, eventId);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable(required = false) @Positive int userId,
                                     @PathVariable(required = false) @Positive int eventId,
                                     @PathVariable(required = false) @Positive int reqId) {
        return service.confirmRequest(userId, eventId, reqId);
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable(required = false) @Positive int userId,
                                    @PathVariable(required = false) @Positive int eventId,
                                    @PathVariable(required = false) @Positive int reqId) {
        return service.rejectRequest(userId, eventId, reqId);
    }
}