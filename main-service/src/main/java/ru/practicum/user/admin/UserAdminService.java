package ru.practicum.user.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.request.model.dto.NewUserRequest;
import ru.practicum.user.model.dto.UserOutDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAdminService {
    final UserAdminRepository repository;

    public List<UserOutDto> findUserByConditions(int[] ids, int from, int size) {
        PageRequest page = pagination(from, size);

        return null;
    }

    public UserOutDto save(NewUserRequest newUser) {
        return null;
    }

    public void delete(int userId) {

    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }
}
