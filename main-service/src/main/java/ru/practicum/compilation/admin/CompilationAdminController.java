package ru.practicum.compilation.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class CompilationAdminController {
    final CompilationAdminService service;

    //Добавление новой подборки
    @PostMapping
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto newCompilation) {
        return service.save(newCompilation);
    }

    //Удаление подборки
    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilationById(@PathVariable int compId) {
        service.deleteCompilation(compId);
        return ResponseEntity.ok().build();
    }

    //Удалить событие из подборки
    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> deleteEventOfCompilation(@PathVariable int compId, @PathVariable int eventId) {
        service.deleteEventOfCompilation(compId, eventId);
        return ResponseEntity.ok().build();
    }

    //Добавить событие в подборку
    @PatchMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> addEventInCompilation(@PathVariable int compId, @PathVariable int eventId) {
        service.addEventInCompilation(compId, eventId);
        return ResponseEntity.ok().build();
    }

    //Открепить подборку на главной странице
    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> unpinCompilation(@PathVariable int compId) {
        service.unpinCompilation(compId);
        return ResponseEntity.ok().build();
    }

    //Прикрепить подборку на главной странице
    @PatchMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> pinCompilation(@PathVariable int compId) {
        service.pinCompilation(compId);
        return ResponseEntity.ok().build();
    }
}
