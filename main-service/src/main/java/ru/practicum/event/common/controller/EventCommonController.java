package ru.practicum.event.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.EventSorting;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.common.service.EventCommonService;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventCommonController {
    final EventCommonService service;
    final StatsClient statClient;

    //Получение событий с возможностью фильтраций
    @GetMapping
    public List<EventShortOutDto> findEvents(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) List<Integer> categories,
                                             @RequestParam(required = false) Boolean paid,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                             @RequestParam(defaultValue = "EVENT_DATE") EventSorting sort,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size,
                                             HttpServletRequest request) {
        statClient.sentStat(request);
        log.info("Получить события с фильтрацией (EventCommonController)");
        return service.findEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
    }

    //Получение подробной информации об опубликованном событии по его id
    @GetMapping("/{id}")
    public EventFullOutDto findEventById(@PathVariable @Positive int id, HttpServletRequest request) {
        statClient.sentStat(request);
        log.info("Получить полную информацию о событии с id = {} (EventCommonController)", id);
        return service.findEventById(id);
    }
}
