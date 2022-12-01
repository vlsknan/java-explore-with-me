package ru.practicum.event.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.event.client.model.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    //Получить статистику
    private ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "uris", uris.get(0),
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    //Получить просмотры
    public int getViews(int eventId) {
        ResponseEntity<Object> responseEntity = getStat(LocalDateTime.of(2010, 12, 31, 0, 0),
                LocalDateTime.now(), List.of("/events/" + eventId), false);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ArrayList<Object> body = (ArrayList<Object>) responseEntity.getBody();
            if (body.size() != 0) {
                Integer hits = 0;
                for (Object obj : body) {
                    Integer hit = (Integer) ((LinkedHashMap) obj).get("hits");
                    hits += hit;
                }
                return hits;
            }
        }
        return 0;
    }

    //Отправить статистику
    public ResponseEntity<Object> sentStat(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        return post("/hit", endpointHit);
    }
}
