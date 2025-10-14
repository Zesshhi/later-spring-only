package ru.practicum.user;

import java.util.List;

interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    UserDto getUser(Long id);

    void deleteUser(Long id);
}