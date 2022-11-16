package ru.practicum.event.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.request.model.dto.UpdateEventRequest;

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
    final EventAdminService service;

    /* Поиск событий с условиями
    users - список id пользователей, чьи события нужно найти
    states список состояний в которых находятся искомые события
    categories - список id категорий в которых будет вестись поиск
    rangeStart - дата и время не раньше которых должно произойти событие
    rangeEnd - дата и время не позже которых должно произойти событие
    from - количество событий, которые нужно пропустить для формирования текущего набора
    size - количество событий в наборе
    */
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
    public EventFullOutDto updateEvent(@PathVariable int eventId, @RequestBody UpdateEventRequest updateEvent) {
        return service.update(eventId, updateEvent);
    }

    //Публикация события
    @PatchMapping("/{eventId}/publish")
    public EventFullOutDto publishEvent(@PathVariable int eventId) {
        return service.publishEvent(eventId);
    }

    //Отклонение события
    @PatchMapping("/{eventId}/reject")
    public EventFullOutDto rejectEvent(@PathVariable int eventId) {
        return service.rejectEvent(eventId);
    }
}
