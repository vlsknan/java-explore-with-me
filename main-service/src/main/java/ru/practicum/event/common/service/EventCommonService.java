package ru.practicum.event.common.service;

import ru.practicum.enums.EventSorting;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCommonService {
    List<EventShortOutDto> findEvents(String text, int[] categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, boolean onlyAvailable, EventSorting sort,
                                      int from, int size);

    EventFullOutDto findEventById(int id);

    void addViewForEvent(EventFullOutDto eventFullOutDto);

    void addViewsForEvents(List<EventShortOutDto> eventsShortDtos);
}
