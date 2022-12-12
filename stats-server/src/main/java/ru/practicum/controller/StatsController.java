package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.service.StatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    final StatService statService;

    //Сохранение информации о том, что к эндпоинту был запрос
    @PostMapping("/hit")
    public ResponseEntity<HttpStatus> saveInfo(@RequestBody EndpointHit endpointHit) {
        log.info("Сохранить информацию о том что к эндпоинты был запрос (StatsController)");
        statService.save(endpointHit);
        return ResponseEntity.ok().build();
    }

    //Получение статистики по посещениям
    @GetMapping("/stats")
    public List<ViewStats> findStat(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получить статистику по посещениям (StatsController)");
        return statService.findStat(start, end, uris, unique);
    }
}
