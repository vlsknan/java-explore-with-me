package ru.practicum.compilation.common.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.compilation.common.service.CompilationCommonService;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.user.model.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationCommonServiceImpl implements CompilationCommonService {
    final CompilationRepository repository;

    @Override
    public List<EventShortOutDto> findCompilation(boolean pinned, int from, int size) {
        PageRequest page = pagination(from, size);
        List<Compilation> compilations = repository.findAllByPinned(pinned, page);

        return null;
    }

    @Override
    public List<EventShortOutDto> findCompilationById(int compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found.", compId)));
        return compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()), ))
                .collect(Collectors.toList());
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
