package ru.practicum.event.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.event.client.model.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatClient {
    final URI uri;
    final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    public StatClient(@Value("${stats-server.url}") String uriStr) {
        this.uri = URI.create(uriStr + "/hit");
    }

    public void sendStats(HttpServletRequest request) throws IOException {
        try {
            HttpRequest statRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "text/html")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            new EndpointHit(
                                    null,
                                    "main-server",
                                    request.getRequestURI(),
                                    request.getRemoteAddr(),
                                    LocalDateTime.now()
                            ).toString()))
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            client.send(statRequest, handler);
            log.info("Данные отправлены на сервер статистики");
        } catch (IOException | InterruptedException ex) {
            throw new IOException("Incorrect httpRequest");
        }
    }
}