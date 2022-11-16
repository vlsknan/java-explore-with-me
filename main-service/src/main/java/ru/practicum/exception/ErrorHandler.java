package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

/*
400 - запрос составлен с ошибкой
403 - не выполнены условия для совершения операции
404 - объект не найден
409 - запрос привод к нарушению целостности данных
500 - внутренняя ошибка сервера
 */

@ControllerAdvice
@Slf4j
public class ErrorHandler {
//
//    @ExceptionHandler(.class)
//    public ErrorResponse handleException( e) {
//        log.error("");
//        return new ErrorResponse();
//    }
}
