package ru.practicum.request;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ItemRequestMapper {

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getUserId(), itemRequest.getDescription());
    }

    public static List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto).toList();
        return itemRequestDtos;
    }

    public static ItemRequest mapToNewUser(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setUserId(itemRequestDto.getUserId());
        itemRequest.setDescription(itemRequestDto.getDescription());

        return itemRequest;
    }
}
