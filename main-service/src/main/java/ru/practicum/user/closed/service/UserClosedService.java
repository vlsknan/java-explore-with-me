package ru.practicum.user.closed.service;

import ru.practicum.request.model.dto.RequestDto;

import java.util.List;

public interface UserClosedService {
    List<RequestDto> getByRequesterId(int userId);

    RequestDto addRequest(int userId, int eventId);

    RequestDto cancelRequest(int userId, int requestId);
}
