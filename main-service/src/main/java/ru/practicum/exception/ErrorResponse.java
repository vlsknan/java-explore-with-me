package ru.practicum.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    List<String> errors = new ArrayList<>();
    String message;
    String reason;
    HttpStatus status;
    String timestamp;

    public ErrorResponse(String message, String reason, HttpStatus status, String timestamp) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}
