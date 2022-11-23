package ru.practicum.request.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventInDto {
    @NotNull
    @Size(min = 20, max = 200)
    String annotation; //Краткое описание
    @NotNull
    CategoryDto category;
    @Size(min = 20, max = 7000)
    @NotNull
    String description; //Полное описание события
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull
    Location location; //Широта и долгота места проведения события
    boolean paid; //Нужно ли оплачивать участие
    int participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    @NotNull
    @Size(min = 3, max = 120)
    String title; //Заголовок
}
