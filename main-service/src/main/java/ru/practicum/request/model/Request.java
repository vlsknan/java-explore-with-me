package ru.practicum.request.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    int id;
    Event event;
    LocalDateTime created;
    User requester;
    @Enumerated(EnumType.STRING)
    State status;
}
