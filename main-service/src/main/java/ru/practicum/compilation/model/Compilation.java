package ru.practicum.compilation.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation { //Подборка событий
    int id;
    String title;
    boolean pinned;
    List<Event> events;
}
