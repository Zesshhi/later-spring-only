package ru.practicum.item;

import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingInItemDto;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemResponseDto mapToItemDto(
            Item item,
            Optional<BookingInItemDto> lastBooking,
            Optional<BookingInItemDto> nextBooking,
            List<CommentDto> comments
    ) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null && lastBooking.isPresent() ? lastBooking.get() : null,
                nextBooking != null && nextBooking.isPresent() ? nextBooking.get() : null,
                comments != null ? comments : Collections.emptyList()
        );
    }


    public static List<ItemDto> mapToItemDto(List<Item> items) {
        List<ItemDto> itemDtos = items.stream().map(ItemMapper::mapToItemDto).toList();
        return itemDtos;
    }

    public static Item mapToNewItem(ItemDto itemDto) {
        Item item = new Item();

        item.setId(itemDto.getId());

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

}
