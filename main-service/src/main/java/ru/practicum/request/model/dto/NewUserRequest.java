package ru.practicum.request.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

//Данные нового пользователя
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    @NotNull
    String email;
    @NotNull
    String name;
}
