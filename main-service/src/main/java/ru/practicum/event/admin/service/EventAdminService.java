package ru.practicum.event.admin.service;

import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    List<EventFullOutDto> findByConditions(int[] users, String[] states, int[] categories, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, int from, int size);

    EventFullOutDto update(int eventId, UpdateEventRequest updateEvent);

    EventFullOutDto publishEvent(int eventId);

    EventFullOutDto rejectEvent(int eventId);
}
