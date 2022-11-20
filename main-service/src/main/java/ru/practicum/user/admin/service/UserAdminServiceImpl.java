package ru.practicum.user.admin.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.model.dto.NewUserRequest;
import ru.practicum.user.model.User;
import ru.practicum.user.model.dto.UserOutDto;
import ru.practicum.user.model.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAdminServiceImpl implements UserAdminService {
    final UserRepository repository;

    @Override
    public List<UserOutDto> findUserByConditions(int[] ids, int from, int size) {
        PageRequest page = pagination(from, size);
        List<User> users = repository.findAllById(ids, page);
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserOutDto save(NewUserRequest newUser) {
        User user = UserMapper.toUser(newUser);
        log.info("Пользователь с email = {} сохранен", user.getEmail());
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public void delete(int userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found.", userId)));
        repository.delete(user);
        log.info("Пользователь с id = {} удален", userId);
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
