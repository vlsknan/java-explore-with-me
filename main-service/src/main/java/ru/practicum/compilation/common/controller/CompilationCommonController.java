package ru.practicum.compilation.common.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.common.service.CompilationCommonServiceImpl;
import ru.practicum.compilation.model.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationCommonController {
    final CompilationCommonServiceImpl service;

    //Получение подборок событий
    @GetMapping
    public List<CompilationDto> findCompilation(@RequestParam(required = false) boolean pinned,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Получить подборки событий (CompilationCommonController)");
        return service.findCompilation(pinned, from, size);
    }

    //Получение подборки событий по его id
    @GetMapping("/{compId}")
    public CompilationDto findCompilationById(@PathVariable @Positive int compId) {
        log.info("Получить данные о подборке с id = {}(CompilationCommonController)", compId);
        return service.findCompilationById(compId);
    }
}
