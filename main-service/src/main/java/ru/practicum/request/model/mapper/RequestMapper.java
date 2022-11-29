package ru.practicum.request.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.dto.RequestDto;

@UtilityClass
public class RequestMapper {

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
