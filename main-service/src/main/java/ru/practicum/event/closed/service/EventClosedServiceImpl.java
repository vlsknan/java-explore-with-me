package ru.practicum.event.closed.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.utility.PageUtility;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.StateEvent;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.client.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.dto.NewEventInDto;
import ru.practicum.event.model.dto.UpdateEventRequest;
import ru.practicum.event.model.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.*;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.dto.RequestDto;
import ru.practicum.request.model.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.model.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class EventClosedServiceImpl implements EventClosedService {
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final RequestRepository requestRepository;
    final CategoryRepository categoryRepository;
    final StatsClient statsClient;

    @Override
    public List<EventShortOutDto> getEventByUser(int userId, int from, int size) {
        PageRequest page = PageUtility.pagination(from, size);
        User initiator = getUserById(userId);

        List<Event> events = eventRepository.findEventByInitiator(initiator, page);
        log.info("Получены события созданные пользователем с id = {}", userId);
        return events.stream()
                .map(e -> EventMapper.toEventShortDto(e, CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator()),
                        requestRepository.countByEventIdAndStatus(e.getId(), StatusRequest.CONFIRMED),
                        statsClient.getViews(e.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullOutDto update(int userId, UpdateEventRequest updateRequest) {
        User initiator = getUserById(userId);
        Event oldEvent = getEventById(updateRequest.getEventId());
        checkEventInitiator(oldEvent, initiator);

        if (oldEvent.getState().equals(StateEvent.CANCELED)) {
            oldEvent.setState(StateEvent.PENDING);
        }
        if (oldEvent.getState().equals(StateEvent.PENDING)) {
            if (updateRequest.getAnnotation() != null) {
                oldEvent.setAnnotation(updateRequest.getAnnotation());
            }
            if (updateRequest.getDescription() != null) {
                oldEvent.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getCategory() != null) {
                Category category = getCategoryById(updateRequest.getCategory());
                oldEvent.setCategory(category);
            }
            if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                oldEvent.setEventDate(updateRequest.getEventDate());
            } else {
                throw new ConditionsNotMet("Event start time in less than 2 hours");
            }
            if (updateRequest.getPaid() != null) {
                oldEvent.setPaid(updateRequest.getPaid());
            }
            if (updateRequest.getParticipantLimit() != null) {
                oldEvent.setParticipantLimit(updateRequest.getParticipantLimit());
            }
            if (updateRequest.getTitle() != null) {
                oldEvent.setTitle(updateRequest.getTitle());
            }
        }
        Event updateEvent = eventRepository.save(oldEvent);
        log.info("Данные о событии с id = {} изменены", updateEvent.getId());
        return getEventFullOutDto(updateEvent);
    }

    @Override
    @Transactional
    public EventFullOutDto create(int userId, NewEventInDto newEvent) {
        User initiator = getUserById(userId);
        Category category = getCategoryById(newEvent.getCategory());

        if (newEvent.getEventDate() != null && newEvent.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            Event event = EventMapper.toEvent(newEvent, category, initiator);
            event.setState(StateEvent.PENDING);
            Event saveEvent = eventRepository.save(event);
            log.info("Событие с id = {} создано", saveEvent.getId());
            return getEventFullOutDto(event);
        }
        throw new ConditionsNotMet("Event start time in less than 2 hours");
    }

    @Override
    public EventFullOutDto getEventById(int userId, int eventId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);
        checkEventInitiator(event, initiator);

        log.info("Получены данные события с id = {}", eventId);
        return getEventFullOutDto(event);
    }

    @Override
    @Transactional
    public EventFullOutDto cancelEvent(int userId, int eventId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);
        checkEventInitiator(event, initiator);

        if (event.getState().equals(StateEvent.PENDING)) {
            event.setState(StateEvent.CANCELED);
            Event newEvent = eventRepository.save(event);
            log.info("Событие с id = {} отменено", eventId);
            return getEventFullOutDto(newEvent);
        }
        throw new ConditionsNotMet(String.format("The event cannot be canceled because its status %s", event.getState()));
    }

    @Override
    public List<RequestDto> getRequestsByUser(int userId, int eventId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);

        List<Request> requests = requestRepository.findRequestByEvent(event);
        log.info("Получены запросы на событие с id = {}", eventId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto confirmRequest(int userId, int eventId, int reqId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);
        Request request = getRequestById(reqId);
        checkEventInitiator(event, initiator);

        if (event.getParticipantLimit() != 0 && event.isRequestModeration()) {
            int limit = event.getParticipantLimit();
            int participantRequest = requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED);
            if (limit > participantRequest) {
                request.setStatus(StatusRequest.CONFIRMED);
                requestRepository.save(request);

                if (limit == ++participantRequest) {
                    requestRepository.findRequestByEvent(event).stream()
                            .filter(r -> r.getStatus() == StatusRequest.PENDING)
                            .forEach(r -> {
                                r.setStatus(StatusRequest.REJECTED);
                                requestRepository.save(r);
                            });
                }
            } else {
                throw new ConditionsNotMet(String.format("The event with id=%s has already reached " +
                        "the request limit", event.getId()));
            }
        }
        log.info("Заявка с id = {} на участие в событии с id = {} подтверждена", reqId, eventId);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    @Transactional
    public RequestDto rejectRequest(int userId, int eventId, int reqId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);
        Request request = getRequestById(reqId);
        checkEventInitiator(event, initiator);

        request.setStatus(StatusRequest.REJECTED);
        requestRepository.save(request);
        log.info("Заявку с id = {} на участие в событии с id = {} отклонена", reqId, eventId);
        return RequestMapper.toRequestDto(request);
    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Event getEventById(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    private Request getRequestById(int reqId) {
        return requestRepository.findById(reqId)
                .orElseThrow(() -> new RequestNotFoundException(reqId));
    }

    private Category getCategoryById(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException(catId));
    }

    private EventFullOutDto getEventFullOutDto(Event event) {
        return EventMapper.toEventFullDto(event, CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.toUserShortDto(event.getInitiator()),
                requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED),
                statsClient.getViews(event.getId()));
    }

    private void checkEventInitiator(Event event, User user) {
        if (event.getInitiator() != user) {
            throw new ConditionsNotMet(String.format("You are not the initiator of the event c id = %s",
                    event.getId()));
        }
    }
}
