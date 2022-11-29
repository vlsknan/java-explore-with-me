package ru.practicum.user.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserOutDto {
    int id;
    @Email
    @NotNull
    String email;
    @NotNull
    String name;
}
