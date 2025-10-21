package ru.practicum.item;

import lombok.Data;

@Data
class Item {
    private Long id;
    private Long ownerId;
    private Long requestId;

    private String name;
    private String description;
    private boolean available;
}