package ru.practicum.compilation.common.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.event.model.dto.EventShortOutDto;

import java.util.List;

public interface CompilationCommonService {
    List<EventShortOutDto> findCompilation(boolean pinned, int from, int size);
    List<EventShortOutDto> findCompilationById(int compId);
}
