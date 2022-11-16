package ru.practicum.event.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.request.model.dto.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventAdminService {
    final EventAdminRepository repository;

    public List<EventFullOutDto> findByConditions(int[] users, String[] states, int[] categories, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, int from, int size) {
        PageRequest page = pagination(from, size);
        return null;
    }

    public EventFullOutDto update(int eventId, UpdateEventRequest updateEvent) {
        return null;
    }

    public EventFullOutDto publishEvent(int eventId) {
        return null;
    }

    public EventFullOutDto rejectEvent(int eventId) {
        return null;
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
