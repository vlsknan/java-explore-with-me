package ru.practicum.compilation.admin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.CompilationNotFoundException;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.EventNotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationAdminServiceImpl implements CompilationAdminService {
    final CompilationRepository compilationRepository;
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final StatsClient statsClient;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto newCompilation) {
        List<Event> events = newCompilation.getEvents().stream()
                .map(this::getEventById)
                .collect(Collectors.toList());

        Compilation saveCompilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilation, events));
        log.info("?????????? ???????????????? ?? id = {} ??????????????????", saveCompilation.getId());

        List<EventShortOutDto> eventShortOutDtos = events.stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(e.getId())))
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(saveCompilation, eventShortOutDtos);
    }

    @Override
    @Transactional
    public void deleteCompilation(int compId) {
        Compilation compilation = getCompilationById(compId);
        compilationRepository.delete(compilation);
        log.info("???????????????? ?? id = {} ??????????????", compId);
    }

    @Override
    @Transactional
    public void deleteEventOfCompilation(int compId, int eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = getEventById(eventId);

        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.remove(event);
        compilation.setEvents(events);

        compilationRepository.save(compilation);
        log.info("???? ???????????????? ?? id = {} ?????????????? ?????????????? ?? id = {}", compId, event);
    }

    @Override
    @Transactional
    public void addEventInCompilation(int compId, int eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = getEventById(eventId);

        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.add(event);
        compilation.setEvents(events);

        compilationRepository.save(compilation);
        log.info("?? ???????????????? ?? id = {} ?????????????????? ?????????????? ?? id = {}", compId, event);
    }

    @Override
    @Transactional
    public void unpinCompilation(int compId) {
        Compilation compilation = getCompilationById(compId);
        if (compilation.isPinned()) {
            compilation.setPinned(false);
            compilationRepository.save(compilation);
            log.info("???????????????? ?? id = {} ???????????????????? ?? ?????????????? ????????????????", compId);
        } else {
            throw new ConditionsNotMet("The collection is not attached to the main page.");
        }
    }

    @Override
    @Transactional
    public void pinCompilation(int compId) {
        Compilation compilation = getCompilationById(compId);
        if (!compilation.isPinned()) {
            compilation.setPinned(true);
            compilationRepository.save(compilation);
            log.info("???????????????? ?? id = {} ?????????????????????? ???? ?????????????? ????????????????", compId);
        } else {
            throw new ConditionsNotMet("The collection is already pinned on the main page.");
        }
    }

    private Compilation getCompilationById(int id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(id));
    }

    private Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }
}
