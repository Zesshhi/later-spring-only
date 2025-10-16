package ru.practicum.user;


import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static List<UserDto> mapToUserDto(List<User> users) {
        List<UserDto> userDtos = users.stream().map(UserMapper::mapToUserDto).toList();
        return userDtos;
    }

    public static User mapToNewUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }
}
