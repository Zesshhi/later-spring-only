package ru.practicum.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.ItemAnswerDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private long id;

    @NotBlank
    private String description;

    private LocalDateTime created;

    private List<ItemAnswerDto> items = new ArrayList<>();
}
