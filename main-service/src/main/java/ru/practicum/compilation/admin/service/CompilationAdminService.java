package ru.practicum.compilation.admin.service;

import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;

public interface CompilationAdminService {
    CompilationDto save(NewCompilationDto newCompilation);

    void deleteCompilation(int compId);

    void deleteEventOfCompilation(int compId, int eventId);

    void addEventInCompilation(int compId, int eventId);

    void unpinCompilation(int compId);

    void pinCompilation(int compId);
}
