package ru.practicum.request.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.request.model.dto.RequestDto;
import ru.practicum.user.model.User;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(ParticipationRequestDto requestDto, Event event, User requester) {
        return Request.builder()
                .id(requestDto.getId())
                .created(requestDto.getCreated())
                .event(event)
                .requester(requester)
                .status(requestDto.getStatus())
                .build();
    }

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
