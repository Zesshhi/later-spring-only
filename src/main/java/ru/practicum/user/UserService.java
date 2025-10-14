package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.InvalidEmailException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserRepository repository;

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto saveUser(UserDto user) {
        validateEmail(user.getEmail());

        User newUser = repository.save(UserMapper.mapToNewUser(user));
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto updateUser(Long id, UserDto user) {

        UserDto oldUser = getUser(id);

        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        } else {
            emailExists(user.getEmail());
        }

        User updatedUser = repository.update(id, UserMapper.mapToNewUser(user));
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        repository.delete(id);
    }

    public UserDto getUser(Long id) {

        Optional<User> user = repository.get(id);

        if (!user.isPresent()) {
            throw new NotFoundException("Пользователь не найден");
        }

        return UserMapper.mapToUserDto(user.get());
    }

    public void validateEmail(String email) {
        if (email == null) {
            throw new InvalidDataException("Email is null");
        }

        emailExists(email);
    }

    public void emailExists(String email) {
        boolean emailExists = repository.findAll().stream().anyMatch(u -> u.getEmail().equals(email));

        if (emailExists) {
            throw new InvalidEmailException("Email already exists");
        }
    }
}