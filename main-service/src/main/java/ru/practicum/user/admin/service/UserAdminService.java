package ru.practicum.user.admin.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.request.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserOutDto;

import java.util.List;

public interface UserAdminService {
    List<UserOutDto> findUserByConditions(int[] ids, int from, int size);

    public UserOutDto save(NewUserRequest newUser);

    void delete(int userId);
}
