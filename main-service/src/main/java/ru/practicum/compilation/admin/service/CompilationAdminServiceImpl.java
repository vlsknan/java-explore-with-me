package ru.practicum.compilation.admin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.user.model.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationAdminServiceImpl implements CompilationAdminService{
    final CompilationRepository compilationRepository;
    final EventRepository eventRepository;

    public CompilationDto save(NewCompilationDto newCompilation) {
        List<Event> events = newCompilation.getEvents().stream()
                .map(id -> eventRepository.findById(id).get())
                .collect(Collectors.toList());
        List<EventShortOutDto> eventShortOutDtos = events.stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()), )) ////добавить из статистики confirmedRequests и views
                .collect(Collectors.toList());
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilation, events));
        log.info("Новая подборка с id = {} сохранена", compilation.getId());
        return CompilationMapper.toCompilationDto(compilation, eventShortOutDtos);
    }

    public void deleteCompilation(int compId) {
        Compilation compilation =  getCompilationById(compId);
        compilationRepository.delete(compilation);
        log.info("Подборка с id = {} удалена", compId);
    }

    public void deleteEventOfCompilation(int compId, int eventId) {
        Compilation compilation =  getCompilationById(compId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", compId)));
        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Из подборки с id = {} удалено событие с id = {}", compId, event);
    }

    public void addEventInCompilation(int compId, int eventId) {
        Compilation compilation =  getCompilationById(compId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", compId)));
        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("В подборку с id = {} добавлено событие с id = {}", compId, event);
    }

    public void unpinCompilation(int compId) {
        Compilation compilation = getCompilationById(compId);
        if (compilation.isPinned()) {
            compilation.setPinned(false);
            log.info("Подборка с id = {} откреплена с главной страницы", compId);
        }
        throw new ConditionsNotMet("The collection is not attached to the main page.");
    }

    public void pinCompilation(int compId) {
        Compilation compilation = getCompilationById(compId);
        if (!compilation.isPinned()) {
            compilation.setPinned(true);
            log.info("Подборка с id = {} прикреплена на главную страницу", compId);
        }
        throw new ConditionsNotMet("The collection is already pinned on the main page.");
    }

    private Compilation getCompilationById(int id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found.", id)));
    }
}
