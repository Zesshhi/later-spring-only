package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.InvalidEmailException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto saveUser(UserDto user) {
        validateEmail(user.getEmail());

        User newUser = repository.save(UserMapper.mapToNewUser(user));
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto updateUser(Long id, UserDto newUser) {
        UserDto user = getUser(id);

        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }

        if (newUser.getEmail() != null) {
            emailExists(newUser.getEmail());
            user.setEmail(newUser.getEmail());
        }


        User updatedUser = repository.save(UserMapper.mapToNewUser(user));
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    public UserDto getUser(Long id) {
        return UserMapper.mapToUserDto(getUserById(id));
    }

    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден"));
    }

    public void validateEmail(String email) {
        if (email == null) {
            throw new InvalidDataException("Email is null");
        }

        emailExists(email);
    }

    public void emailExists(String email) {
        boolean emailExists = repository.existsByEmail(email);

        if (emailExists) {
            throw new InvalidEmailException("Email already exists");
        }
    }
}