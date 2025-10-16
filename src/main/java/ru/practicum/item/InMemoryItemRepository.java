package ru.practicum.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


@Repository
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new HashMap<>(5);
    private final Map<Long, List<Item>> itemsByOwner = new HashMap<>();


    public Item save(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        itemsByOwner.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>()).add(item);
        return item;
    }


    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }


    public List<Item> getByOwner(Long userId) {
        return itemsByOwner.getOrDefault(userId, List.of());
    }


    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.trim().toLowerCase();

        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    private long getId() {
        long lastId = items.values().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}