package ru.practicum.compilation.common.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.utility.PageUtility;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.exception.model.CompilationNotFoundException;
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
    final StatsClient statsClient;

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        PageRequest page = PageUtility.pagination(from, size);
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, page);
        log.info("Получены все подборки");
        return compilations.stream()
                .map(c -> CompilationMapper.toCompilationDto(c, getEventShort(c)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));
        log.info("Получили подборку с id = {}", compId);
        return CompilationMapper.toCompilationDto(compilation, getEventShort(compilation));
    }

    private List<EventShortOutDto> getEventShort(Compilation compilation) {
        List<EventShortOutDto> events = compilation.getEvents().stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(e.getId())))
                .collect(Collectors.toList());
        return events;
    }
}
