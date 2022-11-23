package ru.practicum.event.closed.service;

import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.request.model.dto.NewEventInDto;
import ru.practicum.request.model.dto.UpdateEventRequest;
import ru.practicum.request.model.dto.RequestDto;

import java.util.List;

public interface EventClosedService {
    List<EventShortOutDto> findEventByUser(int userId, int from, int size);

    EventFullOutDto update(int userId, UpdateEventRequest updateRequest);

    EventFullOutDto create(int userId, NewEventInDto newEvent);

    EventFullOutDto findEventById(int userId, int eventId);

    EventFullOutDto cancelEvent(int userId, int eventId);

    List<RequestDto> findRequestsByUser(int userId, int eventId);

    RequestDto confirmRequest(int userId, int eventId, int reqId);

    RequestDto rejectRequest(int userId, int eventId, int reqId);


}
