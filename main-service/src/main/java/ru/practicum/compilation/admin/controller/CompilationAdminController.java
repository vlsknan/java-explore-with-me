package ru.practicum.compilation.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.admin.service.CompilationAdminServiceImpl;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class CompilationAdminController {
    final CompilationAdminServiceImpl service;

    //Добавление новой подборки
    @PostMapping
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto newCompilation) {
        log.info("Добавить новую подборку с заголовком {} (CompilationAdminController)", newCompilation.getTitle());
        return service.save(newCompilation);
    }

    //Удаление подборки
    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilationById(@PathVariable @Positive int compId) {
        log.info("Удалить подборку с id ={} (CompilationAdminController)", compId);
        service.deleteCompilation(compId);
        return ResponseEntity.ok().build();
    }

    //Удалить событие из подборки
    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> deleteEventOfCompilation(@PathVariable @Positive int compId,
                                                               @PathVariable @Positive int eventId) {
        log.info("Удалить событие с id = {} из подборки с id = {} (CompilationAdminController)", eventId, compId);
        service.deleteEventOfCompilation(compId, eventId);
        return ResponseEntity.ok().build();
    }

    //Добавить событие в подборку
    @PatchMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> addEventInCompilation(@PathVariable @Positive int compId,
                                                            @PathVariable @Positive int eventId) {
        log.info("Добавить событие с id = {} в подборку с id = {} (CompilationAdminController)", eventId, compId);
        service.addEventInCompilation(compId, eventId);
        return ResponseEntity.ok().build();
    }

    //Открепить подборку на главной странице
    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> unpinCompilation(@PathVariable @Positive int compId) {
        log.info("Открепить подборку с id = {} с главной страницы (CompilationAdminController)", compId);
        service.unpinCompilation(compId);
        return ResponseEntity.ok().build();
    }

    //Прикрепить подборку на главной странице
    @PatchMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> pinCompilation(@PathVariable @Positive int compId) {
        log.info("Прикрепить подборку с id = {} на главную страницу (CompilationAdminController)", compId);
        service.pinCompilation(compId);
        return ResponseEntity.ok().build();
    }
}
