package ru.practicum.review;

import lombok.Data;

@Data
public class Review {
    private Long id;
    private Long userId;
    private Long itemId;
    private String content;
}
