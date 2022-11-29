package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventInDto {
    @NotNull(message = "Annotation name cannot be null")
    @NotBlank(message = "Annotation name cannot be blank")
    @Size(min = 20, max = 2000)
    String annotation; //Краткое описание
    @NotNull(message = "Category id cannot be null")
    int category;
    @Size(min = 20, max = 7000)
    @NotNull(message = "Description cannot be null")
    String description; //Полное описание события
    @NotNull(message = "Event date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Location cannot be null")
    Location location; //Широта и долгота места проведения события
    boolean paid; //Нужно ли оплачивать участие
    int participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    @NotNull(message = "Title name cannot be null")
    @Size(min = 3, max = 120)
    String title; //Заголовок
}
