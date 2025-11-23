package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingCreateDto;
import ru.practicum.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final String USER_HEADER_NAME = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestHeader(USER_HEADER_NAME) Long bookerId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto
    ) {
        return bookingService.createBooking(bookingCreateDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingItemStatus(
            @RequestHeader(USER_HEADER_NAME) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam(required = true) Boolean approved
    ) {
        return bookingService.updateBookingItemStatusByOwner(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader(USER_HEADER_NAME) Long requestUserId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getBooking(requestUserId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(
            @RequestHeader(USER_HEADER_NAME) Long requestUserId,
            @RequestParam(required = false, defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByBooker(requestUserId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(
            @RequestHeader(USER_HEADER_NAME) Long requestUserId,
            @RequestParam(required = false, defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByOwner(requestUserId, state);
    }
}
