package ru.practicum.compilation.common.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.event.model.dto.EventShortOutDto;

import java.util.List;

public interface CompilationCommonService {
    List<CompilationDto> findCompilation(boolean pinned, int from, int size);
    CompilationDto findCompilationById(int compId);
}
