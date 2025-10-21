package ru.practicum.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PermissionDeniedException;
import ru.practicum.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final InMemoryItemRepository itemRepository;
    private final UserService userService;


    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        userService.getUser(userId);

        Item item = ItemMapper.mapToNewItem(itemDto, userId, null);
        Item newItem = itemRepository.save(item);

        return ItemMapper.mapToItemDto(newItem);
    }


    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {

        userService.getUser(userId);

        Item existingItem = itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена."));


        validateOwner(userId, existingItem);

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }


    private void validateOwner(Long userId, Item item) {
        if (!item.getOwnerId().equals(userId)) {
            throw new PermissionDeniedException("User id = " + userId + " is not the owner of item");
        }
    }


    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " not found"));

        return ItemMapper.mapToItemDto(item);
    }


    public List<ItemDto> getItemsByOwner(Long userId) {

        userService.getUser(userId);

        return itemRepository.getByOwner(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }


    public List<ItemDto> searchItems(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}