package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/*
400 - запрос составлен с ошибкой
403 - не выполнены условия для совершения операции
404 - объект не найден
409 - запрос привод к нарушению целостности данных
500 - внутренняя ошибка сервера
 */

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

//    @ExceptionHandler(BadRequestException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleException(BadRequestException e) {
//        log.error("400: {}", e.getMessage(), e.getCause());
//        return ApiError.builder()
//                .errors(List.of(Arrays.toString(e.getStackTrace())))
//                .message(e.getMessage())
//                .reason("The request was made with an error")
//                .status("BAD_REQUEST")
//                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
//                .build();
//    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestException(final RuntimeException e) {
        log.error("400: {}", e.getMessage(), e.getCause());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .message(e.getMessage())
                .reason("The request was made with an error")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler(ConditionsNotMet.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleException(ConditionsNotMet e) {
        log.error("403: {}", e.getMessage(), e.getCause());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .message(e.getMessage())
                .reason("Conditions for the transaction are not met")
                .status("FORBIDDEN")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleException(NotFoundException e) {
        log.error("404: {}", e.getMessage(), e.getCause());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status("NOT_FOUND")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleException(ConflictException e) {
        log.error("409: {}", e.getMessage(), e.getCause());
        ApiError apiError = ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .message(e.getMessage())
                .reason("Request conflicts with the current state of the server")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        return apiError;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(RuntimeException e) {
        log.error("500: {}", e.getMessage(), e.getCause());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .message(e.getMessage())
                .reason("Error occurred")
                .status("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
