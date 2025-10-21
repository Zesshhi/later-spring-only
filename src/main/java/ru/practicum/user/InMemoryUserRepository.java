package ru.practicum.user;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryUserRepository {
    private final Map<Long, User> users = new HashMap<>(5);

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(Long id, User user) {
        users.replace(user.getId(), user);
        user.setId(id);
        return user;
    }

    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void delete(Long id) {
        users.remove(id);
    }


    private long getId() {
        long lastId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}