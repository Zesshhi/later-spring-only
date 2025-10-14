package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.InvalidEmailException;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public UserDto saveUser(UserDto user) {
        validateEmail(user.getEmail());

        User newUser = repository.save(UserMapper.mapToNewUser(user));
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {

        User oldUser = repository.get(id);

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

    @Override
    public void deleteUser(Long id) {
        repository.delete(id);
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.mapToUserDto(repository.get(id));
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