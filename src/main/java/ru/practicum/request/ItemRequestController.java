package ru.practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_HEADER_NAME = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto createRequest(
            @RequestHeader(USER_HEADER_NAME) Long userId,
            @Valid @RequestBody ItemRequestCreateDto createDto
    ) {
        return itemRequestService.createRequest(createDto, userId);
    }

    @GetMapping("/request")
    public List<ItemRequestResponseDto> getUserRequests(
            @RequestHeader(USER_HEADER_NAME) Long userId
    ) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/requests/all")
    public List<ItemRequestResponseDto> getOtherUsersRequests(
            @RequestHeader(USER_HEADER_NAME) Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/request/{requestId}")
    public ItemRequestResponseDto getRequestById(
            @RequestHeader(USER_HEADER_NAME) Long userId,
            @PathVariable Long requestId
    ) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
