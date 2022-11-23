package ru.practicum.event.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.EventSorting;
import ru.practicum.event.client.StatClient;
import ru.practicum.event.common.service.EventCommonServiceImpl;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventCommonController {
    final EventCommonServiceImpl service;
    final StatClient statClient;

    //Получение событий с возможностью фильтраций
    @GetMapping
    public List<EventShortOutDto> findEvents(@RequestParam String text, @RequestParam int[] categories,
                                             @RequestParam Boolean paid,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                             @RequestParam(defaultValue = "EVENT_DATE") EventSorting sort,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size,
                                             HttpServletRequest request) throws IOException {
        statClient.sendStats(request);
        log.info("Получить события с фильтрацией (EventCommonController)");
        List<EventShortOutDto> eventShortOutDto = service.findEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        service.addViewsForEvents(eventShortOutDto);
        return eventShortOutDto;
    }

    //Получение подробной информации об опубликованном событии по его id
    @GetMapping("/{id}")
    public EventFullOutDto findEventById(@PathVariable @Positive int id, HttpServletRequest request) throws IOException {
        statClient.sendStats(request);
        log.info("Получить полную информацию о событии с id = {} (EventCommonController)", id);
        EventFullOutDto eventFullOutDto = service.findEventById(id);
        service.addViewForEvent(eventFullOutDto);
        return eventFullOutDto;
    }
}
