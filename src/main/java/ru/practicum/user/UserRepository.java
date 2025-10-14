package ru.practicum.user;

import java.util.List;

interface UserRepository {
    List<User> findAll();

    User save(User user);

    User update(Long id, User user);

    User get(Long id);

    void delete(Long id);
}