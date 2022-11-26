package ru.practicum.event.admin.service;

import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.UpdateEventRequest;

import java.util.List;

public interface EventAdminService {
    List<EventFullOutDto> searchEvents(List<Integer> users, List<String> states, List<Integer> categories, String rangeStart,
                                       String rangeEnd, Integer from, Integer size);

    EventFullOutDto update(int eventId, UpdateEventRequest updateEvent);

    EventFullOutDto publishEvent(int eventId);

    EventFullOutDto rejectEvent(int eventId);
}
