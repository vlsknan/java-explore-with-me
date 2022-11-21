package ru.practicum.event.client.model.mapper;

import ru.practicum.event.client.model.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public class EndpointHitMapper {
    public static EndpointHitDto requestToHit(HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
