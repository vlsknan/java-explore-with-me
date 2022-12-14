package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventRequest {
    @NotNull
    int eventId; //Идентификатор события
    String annotation; //Краткое описание
    Integer category;
    String description; //Полное описание события
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    Boolean paid; //Нужно ли оплачивать участие
    Integer participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    String title; //Заголовок
}
