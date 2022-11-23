package ru.practicum.event.admin.service;

import com.querydsl.core.types.dsl.BooleanExpression;
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
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.request.model.dto.UpdateEventRequest;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.*;
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

        StateEvent[] stateEvents = null;
        if (states != null) {
            stateEvents = Arrays.stream(states)
                    .map(this::mapToState)
                    .toArray(StateEvent[]::new);
        }
        Optional<BooleanExpression> conditions = getFinalCondition(users, stateEvents, categories, rangeStart, rangeEnd);

        log.info("События по определенным параметрам найдены");
        return conditions
                .map(c -> eventRepository.findAll(c, page).getContent())
                .orElseGet(() -> eventRepository.findAll(page).getContent()).stream()
                .map(e -> EventMapper.toEventFullDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED)))
                .collect(Collectors.toList());
    }

    public EventFullOutDto update(int eventId, UpdateEventRequest updateEvent) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
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
        log.info("Событие с id = {} изменено", eventId);
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
            log.info("Событие с id = {} опубликовано", eventId);
            return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.toUserShortDto(event.getInitiator()),
                    requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED));
        }
        throw new ConditionsNotMet("Only pending or canceled events can be changed");
    }

    public EventFullOutDto rejectEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        if (event.getEventDate().isAfter(event.getPublishedOn().plusHours(1)) && event.getState().equals("PENDING")) {
            event.setState(StateEvent.CANCELED);
            log.info("Событие с id = {} отклонено", eventId);
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

    private StateEvent mapToState(String state) {
        try {
            return StateEvent.valueOf(state);
        } catch (BadRequestException e) {
            throw new BadRequestException(String.format("State is unsupported: %s", state));
        }
    }

    private Optional<BooleanExpression> getFinalCondition(int[] users, StateEvent[] states, int[] categories,
                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<BooleanExpression> conditions = new ArrayList<>();
        QEvent event = QEvent.event;

        if (users != null) {
            List<Integer> userIds = Arrays.stream(users)
                    .mapToObj(Integer::valueOf)
                    .collect(Collectors.toList());
            conditions.add(event.initiator.id.in(userIds));
        }
        if (states != null) {
            conditions.add(event.state.in(states));
        }
        if (categories != null) {
            List<Integer> catIds = Arrays.stream(categories)
                    .mapToObj(Integer::valueOf)
                    .collect(Collectors.toList());
            conditions.add(event.category.id.in(catIds));
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.before(rangeEnd));
        }
        return conditions.stream()
                .reduce(BooleanExpression::and);
    }

}
