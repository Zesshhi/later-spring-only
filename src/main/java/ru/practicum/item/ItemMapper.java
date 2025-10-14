package ru.practicum.item;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getUserId(), item.getUrl(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        items.forEach(item -> itemDtos.add(mapToItemDto(item)));

        return itemDtos;
    }

    public static Item mapToNewItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setUserId(itemDto.getUserId());
        item.setUrl(itemDto.getUrl());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());

        return item;
    }

}
