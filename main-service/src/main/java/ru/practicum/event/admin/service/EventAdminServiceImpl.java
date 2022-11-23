package ru.practicum.event.admin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.enums.StateEvent;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.event.model.dto.UpdateEventRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;

    /* Поиск событий с условиями
    users - список id пользователей, чьи события нужно найти
    states - список состояний в которых находятся искомые события
    categories - список id категорий в которых будет вестись поиск
    rangeStart - дата и время не раньше которых должно произойти событие
    rangeEnd - дата и время не позже которых должно произойти событие
    from - количество событий, которые нужно пропустить для формирования текущего набора
    size - количество событий в наборе
    */
    public List<EventFullOutDto> findByConditions(int[] users, String[] states, int[] categories, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, int from, int size) {
        PageRequest page = pagination(from, size);
        List<Event> events = new ArrayList<>();
        if (users.length != 0 && states.length != 0 && categories.length != 0) {
            events = eventRepository.findAllByParam(users, states, categories, rangeStart, rangeEnd, page);
        } else if (users.length == 0 && states.length != 0 && categories.length != 0) {
            events = eventRepository.findAllWithoutUsers(states, categories, rangeStart, rangeEnd, page);
        } else if (users.length != 0 && states.length == 0 && categories.length != 0) {
            events = eventRepository.findAllWithoutState(users, categories, rangeStart, rangeEnd, page);
        } else if (users.length != 0 && states.length != 0 && categories.length == 0) {
            events = eventRepository.findAllWithoutCategory(users, states, rangeStart, rangeEnd, page);
        }

        List<EventFullOutDto> res = events.stream()
                    .map(e -> EventMapper.toEventFullDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                            UserMapper.toUserShortDto(e.getInitiator()),
                            requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED)))
                    .collect(Collectors.toList());
        return res;
    }

    public EventFullOutDto update(int eventId, UpdateEventRequest updateEvent) {
        Event oldEvent = eventRepository.findById(eventId)
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
        Event event = eventRepository.save(oldEvent);
        return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
    }

    public EventFullOutDto publishEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        log.error("Событие с id = {} не найдено", eventId);
        if (event.getEventDate().isAfter(event.getPublishedOn().plusHours(1)) && event.getState().equals("PENDING")) {
            event.setState(StateEvent.PUBLISHED);
            log.info("Событие опубликовано");
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()),
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet("Only pending or canceled events can be changed");
    }

    public EventFullOutDto rejectEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        log.error("Событие с id = {} не найдено", eventId);
        if (event.getEventDate().isAfter(event.getPublishedOn().plusHours(1)) && event.getState().equals("PENDING")) {
            event.setState(StateEvent.CANCELED);
            log.info("Событие отклонено");
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()),
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet("Only pending or canceled events can be changed");
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
