package ru.practicum.event.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.admin.service.EventAdminService;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.UpdateEventRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    final EventAdminService service;

    //Поиск событий
    @GetMapping
    public List<EventFullOutDto> findByConditions(@RequestParam(required = false) List<Integer> users,
                                                  @RequestParam(required = false) List<String> states,
                                                  @RequestParam(required = false) List<Integer> categories,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поиск событий с фильтрацией (EventAdminController)");
        return service.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    //Редактирование события
    @PutMapping("/{eventId}")
    public EventFullOutDto updateEvent(@PathVariable @Positive int eventId, @RequestBody UpdateEventRequest updateEvent) {
        log.info("Редактировать событие с id = {} (EventAdminController)", eventId);
        return service.update(eventId, updateEvent);
    }

    //Публикация события
    @PatchMapping("/{eventId}/publish")
    public EventFullOutDto publishEvent(@PathVariable @Positive int eventId) {
        log.info("Опубликовать событие с id = {} (EventAdminController)", eventId);
        return service.publishEvent(eventId);
    }

    //Отклонение события
    @PatchMapping("/{eventId}/reject")
    public EventFullOutDto rejectEvent(@PathVariable @Positive int eventId) {
        log.info("Отклонить событие с id = {} (EventAdminController)", eventId);
        return service.rejectEvent(eventId);
    }
}
