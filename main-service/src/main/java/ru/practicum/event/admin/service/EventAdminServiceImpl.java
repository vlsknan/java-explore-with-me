package ru.practicum.event.admin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.enums.StateEvent;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.model.dto.UpdateEventRequest;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    final EventRepository repository;

    public List<EventFullOutDto> findByConditions(int[] users, String[] states, int[] categories, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, int from, int size) {
        PageRequest page = pagination(from, size);
        List<Event> events = repository.findAllByParam(users, states, categories, rangeStart, rangeEnd, page);
        return null;
    }

    public EventFullOutDto update(int eventId, UpdateEventRequest updateEvent) {
        Event oldEvent = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        log.error("Событие с id = {} не найдено", eventId);
        if (updateEvent.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            oldEvent.setCategory(CategoryMapper.toCategory(updateEvent.getCategory()));
        }
        if (updateEvent.getDescription() != null) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            oldEvent.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getTitle() != null) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        Event event = repository.save(oldEvent);
        //добавить из статистики confirmedRequests и views
        return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()), );
    }

    public EventFullOutDto publishEvent(int eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        log.error("Событие с id = {} не найдено", eventId);
        if (event.getEventDate().isAfter(event.getPublishedOn().plusHours(1)) && event.getState().equals("PENDING")) {
            event.setState(StateEvent.PUBLISHED);
            log.info("Событие опубликовано");
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()), );
        }
        throw new ConditionsNotMet("Only pending or canceled events can be changed");
        log.error("Событие не может быть опубликовано, нне выполнены условия выполнения операции");
    }

    public EventFullOutDto rejectEvent(int eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        log.error("Событие с id = {} не найдено", eventId);
        if (event.getEventDate().isAfter(event.getPublishedOn().plusHours(1)) && event.getState().equals("PENDING")) {
            event.setState(StateEvent.CANCELED);
            log.info("Событие отклонено");
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()), );
        }
        throw new ConditionsNotMet("Only pending or canceled events can be changed");
        log.error("Событие не может быть опубликовано, нне выполнены условия выполнения операции");
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
