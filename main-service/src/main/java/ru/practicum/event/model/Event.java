package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.enums.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    int id;
    String annotation;
    Category category;
    User initiator;
    String title;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    boolean paid;
    int participantLimit;
    LocalDateTime publishedOn;
    State state;
    float locationLatitude;
    float locationLongitude;
    boolean requestModeration;
}
