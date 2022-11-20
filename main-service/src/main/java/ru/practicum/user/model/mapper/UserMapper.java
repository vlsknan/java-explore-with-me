package ru.practicum.user.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.dto.NewUserRequest;
import ru.practicum.user.model.User;
import ru.practicum.user.model.dto.UserInDto;
import ru.practicum.user.model.dto.UserOutDto;
import ru.practicum.user.model.dto.UserShortDto;

@UtilityClass
public class UserMapper {
    public static UserOutDto toUserDto(User user) {
        return UserOutDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserInDto userInDto) {
        return User.builder()
                .name(userInDto.getName())
                .email(userInDto.getEmail())
                .build();
    }

    public static User toUser(NewUserRequest newUser) {
        return User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();
    }
}
