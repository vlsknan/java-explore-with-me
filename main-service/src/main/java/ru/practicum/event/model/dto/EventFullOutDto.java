package ru.practicum.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.enums.Status;
import ru.practicum.event.model.Location;
import ru.practicum.user.model.dto.UserShortDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullOutDto {
    int id;
    String annotation; //Краткое описание
    CategoryDto category;
    int confirmedRequests; //Количество одобренных заявок на участие в данном событии
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    String description; //Полное описание события
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    UserShortDto initiator; //Пользователь (краткая информация)
    Location location; //Широта и долгота места проведения события
    boolean paid; //Нужно ли оплачивать участие
    int participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    @Enumerated(EnumType.STRING)
    Status state; //Список состояний жизненного цикла события
    String title; //Заголовок
    int views; //Количество просмотров события
}
