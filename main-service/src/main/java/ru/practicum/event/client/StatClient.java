package ru.practicum.event.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.RequestEntity.post;

@FieldDefaults(level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class StatClient {
    final RestTemplate rest;

    public ResponseEntity<Object> post(HttpServletRequest request) {
        EndpointHit body = EndpointHit.builder()
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .app("main-service")
                .timestamp(LocalDateTime.now()).build();
        return (ResponseEntity<Object>) RequestEntity.post("/hits", body);
    }

    public ViewStatDto getView(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = Map.of("start", start,
                "end", end,
                "uris", uris,
                "unique", unique);
        return (ViewStatDto) RequestEntity.get("/stats", parameters);
    }
}
