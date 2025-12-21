package ru.practicum.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.request.dto.ItemRequestAnswerDto;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemRequestResponseDto createRequest(ItemRequestCreateDto requestDto, Long userId) {
        User requester = userService.getUserById(userId);

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(requestDto);
        itemRequest.setRequester(requester);

        ItemRequest saved = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(saved, Collections.emptyList());
    }

    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        userService.getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);
        Map<Long, List<ItemRequestAnswerDto>> answers = getAnswersByRequestIds(
                requests.stream().map(ItemRequest::getId).toList()
        );

        return ItemRequestMapper.mapToItemRequestDto(requests, answers);
    }

    public List<ItemRequestResponseDto> getOtherUsersRequests(Long userId, int from, int size) {
        userService.getUserById(userId);

        if (size <= 0 || from < 0) {
            throw new InvalidDataException("Параметры пагинации заданы некорректно.");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNot(userId, pageRequest).stream().toList();
        Map<Long, List<ItemRequestAnswerDto>> answers = getAnswersByRequestIds(
                requests.stream().map(ItemRequest::getId).toList()
        );

        return ItemRequestMapper.mapToItemRequestDto(requests, answers);
    }

    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " не найден"));

        Map<Long, List<ItemRequestAnswerDto>> answers = getAnswersByRequestIds(List.of(request.getId()));
        return ItemRequestMapper.mapToItemRequestDto(request, answers.getOrDefault(request.getId(), Collections.emptyList()));
    }

    private Map<Long, List<ItemRequestAnswerDto>> getAnswersByRequestIds(List<Long> requestIds) {
        if (requestIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Item> items = itemRepository.findAllByRequest_IdIn(requestIds);

        return items.stream()
                .map(item -> new ItemRequestAnswerDto(
                        item.getId(),
                        item.getName(),
                        item.getOwner() != null ? item.getOwner().getId() : null,
                        item.getRequest() != null ? item.getRequest().getId() : null
                ))
                .collect(Collectors.groupingBy(ItemRequestAnswerDto::getRequestId));
    }
}
