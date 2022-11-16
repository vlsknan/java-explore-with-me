package ru.practicum.exception.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Error {
    List<String> errors; //Список стектрейсов или описания ошибок
    String message; //Сообщение об ошибке
    String reason; //Общее описание причины ошибки
    ExceptionStatus status; //Код статуса HTTP-ответа
    LocalDateTime timestamp; //Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}
