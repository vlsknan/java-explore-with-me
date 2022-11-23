package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Mapper;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StatService {
    final StatRepository statRepository;

    public void save(EndpointHit endpointHit) {
        statRepository.save(endpointHit);
        log.info("Информация о запросе к эндпоинту сохранена");
    }

    /*
    start - Дата и время начала диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
    end - Дата и время конца диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
    uris - Список uri для которых нужно выгрузить статистику
    unique - Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     */
    public List<ViewStats> findStat(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(
                URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        LocalDateTime endTime = LocalDateTime.parse(
                URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        List<EndpointHit> endpointHits = new ArrayList<>();
        if (unique) {
            if (uris != null) {
                endpointHits = statRepository.findAllUniqueByUri(startTime, endTime, uris);
            } else {
                endpointHits = statRepository.findAll(startTime, endTime);
            }
        } else {
            if (uris != null) {
                endpointHits = statRepository.findAllNoUniqueByUri(startTime, endTime, uris);
            } else {
                endpointHits = statRepository.findAllNoUnique(startTime, endTime);
            }
        }
        log.info("Получена статистика по посещениям");
        return endpointHits.stream()
                .map(Mapper::toViewStats)
                .collect(Collectors.toList());
    }
}
