package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private static final String USER_HEADER_NAME = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping("/requests")
    public ResponseEntity<Object> createRequest(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @Valid @RequestBody ItemRequestCreateDto createDto
    ) {
        return itemRequestClient.createRequest(userId, createDto);
    }

    @GetMapping("/requests")
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId
    ) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/requests/all")
    public ResponseEntity<Object> getOtherUsersRequests(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @PathVariable @Positive Long requestId
    ) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
