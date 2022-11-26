package ru.practicum.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {
    List<String> errors; //Список стектрейсов или описания ошибок
    String message; //Сообщение об ошибке
    String reason; //Общее описание причины ошибки
    String status; //Код статуса HTTP-ответа
    String timestamp; //Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}
