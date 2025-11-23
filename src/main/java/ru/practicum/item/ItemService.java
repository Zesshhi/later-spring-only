package ru.practicum.item;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.booking.BookingService;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingInItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PermissionDeniedException;
import ru.practicum.item.comment.Comment;
import ru.practicum.item.comment.CommentMapper;
import ru.practicum.item.comment.CommentRepository;
import ru.practicum.item.comment.dto.CommentCreateDto;
import ru.practicum.item.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;


    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User user = userService.getUserById(userId);

        Item item = ItemMapper.mapToNewItem(itemDto);

        item.setOwner(user);

        Item newItem = itemRepository.save(item);

        return ItemMapper.mapToItemDto(newItem);
    }


    public ItemDto updateItem(Long itemId, ItemDto newItem, Long userId) {

        userService.getUser(userId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID=" + itemId + " не найдена."));


        validateOwner(userId, existingItem);

        if (newItem.getName() != null) {
            existingItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            existingItem.setAvailable(newItem.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }


    private void validateOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("User id = " + userId + " is not the owner of item");
        }
    }

    @Transactional
    public ItemResponseDto getItem(Long userId, Long itemId) {

        Item item = getItemById(itemId);

        List<CommentDto> comments = getCommentsByItemId(itemId);

        if (item.getOwner().getId().equals(userId)) {
            return getItemResponseDtoWithBookings(item, comments);
        }

        return ItemMapper.mapToItemDto(item, null, null, comments);
    }

    @Transactional
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id = " + itemId + " not found"));
    }

    @Transactional
    public List<ItemResponseDto> getItemsByOwner(Long userId) {

        userService.getUser(userId);

        List<Item> items = itemRepository.findByOwner_Id(userId).stream().toList();

        Map<Long, List<CommentDto>> commentsMap = getCommentsByItemIds(items);

        List<ItemResponseDto> itemDtos = items.stream()
                .map(item -> getItemResponseDtoWithBookings(
                        item, commentsMap.getOrDefault(item.getId(), Collections.emptyList())))
                .toList();

        return itemDtos;
    }


    public List<ItemDto> searchItems(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByTextContainingIgnoreCase(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(Long authorId, Long itemId, CommentCreateDto commentDto) {
        User author = userService.getUserById(authorId);
        Item item = getItemById(itemId);

        bookingService.validateUserBookedItem(authorId, itemId);

        Comment comment = CommentMapper.mapToNewComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return CommentMapper.mapToCommentDto(commentRepository.findAllByItem_Id(itemId));
    }

    private Map<Long, List<CommentDto>> getCommentsByItemIds(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        return commentRepository.findAllByItem_IdIn(itemIds)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getId));
    }

    private ItemResponseDto getItemResponseDtoWithBookings(Item item, List<CommentDto> comments) {
        LocalDateTime currentDate = LocalDateTime.now();
        BookingInItemDto lastBooking = bookingService.getLastBooking(item.getId(), BookingStatus.APPROVED, currentDate);
        BookingInItemDto nextBooking = bookingService.getNextBooking(item.getId(), BookingStatus.APPROVED, currentDate);
        return ItemMapper.mapToItemDto(item, lastBooking, nextBooking, comments);
    }

}