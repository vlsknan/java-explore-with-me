package ru.practicum.compilation.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationAdminService {
    final CompilationAdminRepository repository;

    public CompilationDto save(NewCompilationDto newCompilation) {
        return null;
    }

    public void deleteCompilation(int compId) {

    }

    public void deleteEventOfCompilation(int compId, int eventId) {

    }

    public void addEventInCompilation(int compId, int eventId) {

    }

    public void unpinCompilation(int compId) {

    }

    public void pinCompilation(int compId) {

    }
}
