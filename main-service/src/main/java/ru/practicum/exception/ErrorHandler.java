package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConditionsNotMet;
import ru.practicum.exception.model.NotFoundException;

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

    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse handleException(BadRequestException e) {
        log.error("400: {}", e.getMessage(), e.getCause());
        return new ErrorResponse(e.getMessage(), "The request was made with an error",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConditionsNotMet.class)
    public ErrorResponse handleException(ConditionsNotMet e) {
        log.error("403: {}", e.getMessage(), e.getCause());
        return new ErrorResponse(e.getMessage(), "Conditions for the transaction are not met",
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleException(NotFoundException e) {
        log.error("404: {}", e.getMessage(), e.getCause());
        return new ErrorResponse(e.getMessage(), "The required object was not found.",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleException(RuntimeException e) {
        log.error("500: {}", e.getMessage(), e.getCause());
        return new ErrorResponse(e.getMessage(), "Error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
