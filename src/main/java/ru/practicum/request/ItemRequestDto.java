package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private long id;
    private Long userId;
    private String description;
}
