package ru.practicum.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.ItemRepository;
import ru.practicum.request.dto.ItemAnswerDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUserById(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setUserId(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(savedRequest, Collections.emptyList());
    }

    public List<ItemRequestDto> getOwnRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByUserId_IdOrderByCreatedDesc(userId);
        Map<Long, List<ItemAnswerDto>> items = getItemsForRequests(requests);

        return mapRequestsWithItems(requests, items);
    }

    public List<ItemRequestDto> getOtherRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByUserId_IdNotOrderByCreatedDesc(userId);
        Map<Long, List<ItemAnswerDto>> items = getItemsForRequests(requests);

        return mapRequestsWithItems(requests, items);
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request id = " + requestId + " not found"));

        List<ItemAnswerDto> items = itemRepository.findAllByRequest_Id(requestId).stream()
                .map(ItemRequestMapper::mapToItemAnswerDto)
                .toList();

        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    private Map<Long, List<ItemAnswerDto>> getItemsForRequests(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();

        if (requestIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return itemRepository.findAllByRequest_IdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(),
                        Collectors.mapping(ItemRequestMapper::mapToItemAnswerDto, Collectors.toList())));
    }

    private List<ItemRequestDto> mapRequestsWithItems(List<ItemRequest> requests, Map<Long, List<ItemAnswerDto>> items) {
        return requests.stream()
                .map(request -> ItemRequestMapper.mapToItemRequestDto(
                        request,
                        items.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .toList();
    }
}
