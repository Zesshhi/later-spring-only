package ru.practicum.request;

import lombok.NoArgsConstructor;
import ru.practicum.item.Item;
import ru.practicum.request.dto.ItemAnswerDto;

import java.util.List;

@NoArgsConstructor
public class ItemRequestMapper {

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemAnswerDto> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }

    public static ItemAnswerDto mapToItemAnswerDto(Item item) {
        return new ItemAnswerDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
