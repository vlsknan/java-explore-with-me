package ru.practicum.compilation.common.service;

import ru.practicum.compilation.model.dto.CompilationDto;

import java.util.List;

public interface CompilationCommonService {
    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);
}
