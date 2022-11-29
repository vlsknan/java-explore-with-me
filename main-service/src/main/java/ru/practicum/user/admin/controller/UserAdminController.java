package ru.practicum.user.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.admin.service.UserAdminService;
import ru.practicum.user.model.dto.UserOutDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class UserAdminController {
    final UserAdminService service;

    //Получение информации о пользователях
    @GetMapping
    public List<UserOutDto> getUserByConditions(@RequestParam int[] ids,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получить информацию о пользователях (UserAdminController)");
        return service.getUserByConditions(ids, from, size);
    }

    //Добавление нового пользователя
    @PostMapping
    public UserOutDto saveUser(@Valid @RequestBody(required = false) NewUserRequest newUser) {
        log.info("Добавить пользователя с электронной почтой '{}' (UserAdminController)", newUser.getEmail());
        return service.save(newUser);
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUserById(@Positive @PathVariable int userId) {
        log.info("Удалить пользователя с id = {} (UserAdminController)", userId);
        service.delete(userId);
        return ResponseEntity.ok().build();
    }
}
