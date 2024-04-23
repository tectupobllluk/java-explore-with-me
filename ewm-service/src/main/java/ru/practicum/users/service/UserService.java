package ru.practicum.users.service;

import ru.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(UserDto user);

    void deleteUser(Long userId);

}
