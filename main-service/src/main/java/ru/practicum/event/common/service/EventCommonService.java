package ru.practicum.event.common.service;

import ru.practicum.enums.EventSorting;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;

import java.util.List;

public interface EventCommonService {
    List<EventShortOutDto> findEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                      String rangeEnd, boolean onlyAvailable, EventSorting sort,
                                      int from, int size);

    EventFullOutDto findEventById(int id);
}
