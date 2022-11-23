package ru.practicum.event.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.admin.service.EventAdminServiceImpl;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.UpdateEventRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    final EventAdminServiceImpl service;

    //Поиск событий
    @GetMapping
    public List<EventFullOutDto> findByConditions(@RequestParam int[] users, @RequestParam String[] states ,
                                                  @RequestParam int[] categories,
                                                  @RequestParam(required = false)
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return service.findByConditions(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    //Редактирование события
    @PutMapping("/{eventId}")
    public EventFullOutDto updateEvent(@PathVariable @Positive int eventId, @RequestBody UpdateEventRequest updateEvent) {
        return service.update(eventId, updateEvent);
    }

    //Публикация события
    @PatchMapping("/{eventId}/publish")
    public EventFullOutDto publishEvent(@PathVariable @Positive int eventId) {
        return service.publishEvent(eventId);
    }

    //Отклонение события
    @PatchMapping("/{eventId}/reject")
    public EventFullOutDto rejectEvent(@PathVariable @Positive int eventId) {
        return service.rejectEvent(eventId);
    }
}
