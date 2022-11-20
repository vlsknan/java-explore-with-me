package ru.practicum.user.admin.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.model.dto.NewUserRequest;
import ru.practicum.user.admin.service.UserAdminServiceImpl;
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
    final UserAdminServiceImpl service;

    //Получение информации о пользователях
    @GetMapping
    public List<UserOutDto> findUserByConditions(@RequestParam int[] ids,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return service.findUserByConditions(ids, from, size);
    }

    //Добавление нового пользователя
    @PostMapping
    public UserOutDto saveUser(@Valid @RequestBody(required = false) NewUserRequest newUser) {
        return service.save(newUser);
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUserById(@Positive @PathVariable int userId) {
        service.delete(userId);
        return ResponseEntity.ok().build();
    }
}
