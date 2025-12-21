package ru.practicum.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.ItemRequestAnswerDto;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestCreateDto requestCreateDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestCreateDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestResponseDto mapToItemRequestDto(
            ItemRequest itemRequest,
            List<ItemRequestAnswerDto> answers
    ) {
        return new ItemRequestResponseDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                answers != null ? answers : Collections.emptyList()
        );
    }

    public static List<ItemRequestResponseDto> mapToItemRequestDto(
            List<ItemRequest> itemRequests,
            Map<Long, List<ItemRequestAnswerDto>> answers
    ) {
        return itemRequests.stream()
                .map(request -> mapToItemRequestDto(request, answers.getOrDefault(request.getId(), Collections.emptyList())))
                .toList();
    }
}
