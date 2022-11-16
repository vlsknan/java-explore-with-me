package ru.practicum.compilation.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.dto.EventShortOutDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationCommonService {
    final CompilationCommonRepository repository;

    public List<EventShortOutDto> findCompilation(boolean pinned, int from, int size) {
        PageRequest page = pagination(from, size);
        return null;
    }


    public List<EventShortOutDto> findCompilationById(int compId) {
        return null;
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
