package ru.practicum.user.closed.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.enums.StateEvent;
import ru.practicum.enums.StatusRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.dto.RequestDto;
import ru.practicum.request.model.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserClosedServiceImpl implements UserClosedService {
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final RequestRepository requestRepository;

    @Override
    public List<RequestDto> findByRequesterId(int userId) {
        getUserById(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto addRequest(int userId, int eventId) {
        User requester = getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found.", eventId)));
        if (event.getInitiator() == requester) {
            throw new ConditionsNotMet("You are the event initiator.");
        }
        checkParamEvent(event, userId);

        Request request = Request.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .status(event.isRequestModeration() ? StatusRequest.PENDING : StatusRequest.CONFIRMED).build();
        log.info("Запрос пользователя с id = {} на участие в событии с id = {} создан", userId, eventId);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(int userId, int requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%s was not found.", requestId)));
        request.setStatus(StatusRequest.CANCELED);
        log.info("Заявка пользователя с id = {} на участие в событии с id = {} отменена пользователем",
                userId, request.getEvent().getId());
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found.", userId)));
    }

    private void checkParamEvent(Event event, int userId) {
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConditionsNotMet(String.format("Event with i=%s not published", event.getId()));
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, event.getId())) {
            throw new ConditionsNotMet(String.format("A request from a user with id=%s to participate in an " +
                    "event with id=%s already exists", userId, event.getId()));
        }
        int limit = event.getParticipantLimit();
        if (limit > 0) {
            int participantRequest = requestRepository.countByEventIdAndStatus(event.getId(), StatusRequest.CONFIRMED);
            if (participantRequest == limit) {
                throw new ConditionsNotMet(String.format("The event with id=%s has already reached " +
                        "the request limit", event.getId()));
            }
        }
    }
}
