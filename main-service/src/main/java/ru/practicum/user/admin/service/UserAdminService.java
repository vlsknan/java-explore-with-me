package ru.practicum.user.admin.service;

import ru.practicum.user.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserOutDto;

import java.util.List;

public interface UserAdminService {
    List<UserOutDto> findUserByConditions(int[] ids, int from, int size);

    UserOutDto save(NewUserRequest newUser);

    void delete(int userId);
}
