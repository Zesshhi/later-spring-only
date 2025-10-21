package ru.practicum.item;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getRequestId());
    }

    public static List<ItemDto> mapToItemDto(List<Item> items) {
        List<ItemDto> itemDtos = items.stream().map(ItemMapper::mapToItemDto).toList();
        return itemDtos;
    }

    public static Item mapToNewItem(ItemDto itemDto, Long userId, Long requestId) {
        Item item = new Item();

        item.setId(itemDto.getId());
        item.setOwnerId(userId);
        item.setRequestId(requestId);

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

}
