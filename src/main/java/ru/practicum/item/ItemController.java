package ru.practicum.item;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.comment.dto.CommentCreateDto;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemResponseDto;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final String USER_HEADER_NAME = "X-Sharer-User-Id";


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(USER_HEADER_NAME) Long userId
    ) {
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader(USER_HEADER_NAME) Long userId
    ) {
        return itemService.updateItem(itemId, itemDto, userId);
    }


    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(
            @RequestHeader(USER_HEADER_NAME) Long userId,
            @PathVariable Long itemId
    ) {
        return itemService.getItem(userId, itemId);
    }


    @GetMapping
    public List<ItemResponseDto> getItemsByOwner(@RequestHeader(USER_HEADER_NAME) Long userId) {
        return itemService.getItemsByOwner(userId);
    }


    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(USER_HEADER_NAME) Long authorId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentDto
    ) {
        return itemService.createComment(authorId, itemId, commentDto);
    }
}