package ru.practicum.compilation.common.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationCommonServiceImpl implements CompilationCommonService {
    final CompilationRepository compilationRepository;
    final RequestRepository requestRepository;

    @Override
    public List<CompilationDto> findCompilation(boolean pinned, int from, int size) {
        PageRequest page = pagination(from, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, page);
        List<EventShortOutDto> events = null;

        for (Compilation compilation : compilations) {
            events = getEventShort(compilation);
        }

        List<EventShortOutDto> finalEvents = events;
        log.info("Получили все подборки");
        return compilations.stream()
                .map(c -> CompilationMapper.toCompilationDto(c, finalEvents))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilationById(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found.", compId)));
        List<EventShortOutDto> events = getEventShort(compilation);
        log.info("получили подборки с if = {}", compId);

        return CompilationMapper.toCompilationDto(compilation, events);
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }

    private List<EventShortOutDto> getEventShort(Compilation compilation) {
        List<EventShortOutDto> events = compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED)))
                .collect(Collectors.toList());
        return events;
    }
}
