package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.dto.ViewStatDto;
import ru.practicum.service.StatService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    final StatService statService;

    //Сохранение информации о том, что к эндпоинту был запрос
    @PostMapping("/hit")
    public ResponseEntity<HttpStatus> saveInfo(@RequestBody EndpointHit endpointHit) {
        statService.save(endpointHit);
        log.info("Вызван метод saveInfo() в StatsController");
        return ResponseEntity.ok().build();
    }

    //Получение статистики по посещениям
    @GetMapping("/stats")
    public ViewStatDto findStat(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(required = false) String[] uris,
                                @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Вызван метод findStat() в StatsController");
        return statService.findStat(start, end, uris, unique);
    }
}
