package ru.practicum.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.booking.dto.BookingCreateDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private static final String USER_HEADER_NAME = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto
    ) {
        return bookingClient.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingItemStatus(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved
    ) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @PathVariable @Positive Long bookingId
    ) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(
            @RequestHeader(USER_HEADER_NAME) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingClient.getOwnerBookings(userId, state);
    }
}
