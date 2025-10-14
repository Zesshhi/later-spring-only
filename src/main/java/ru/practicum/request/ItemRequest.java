package ru.practicum.request;

import lombok.Data;

@Data
public class ItemRequest {
    private long id;
    private Long userId;
    private String description;

}
