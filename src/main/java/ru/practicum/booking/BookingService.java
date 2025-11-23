package ru.practicum.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.booking.dto.BookingCreateDto;
import ru.practicum.booking.dto.BookingInItemDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ObjectNotAvailableException;
import ru.practicum.exception.PermissionDeniedException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Transactional
    public BookingResponseDto createBooking(BookingCreateDto bookingDto, Long bookerId) {

        if (bookingDto.getStart().isEqual(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ObjectNotAvailableException("Дата начала должна быть раньше окончания.");
        }

        User booker = userService.getUserById(bookerId);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item с ID=" + bookingDto.getItemId() + " не найден"));

        if (!item.isAvailable()) {
            throw new ObjectNotAvailableException("Вещь с id = " + bookingDto.getItemId() + " не доступна.");
        }

        Booking booking = BookingMapper.mapToNewBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional
    public BookingResponseDto updateBookingItemStatusByOwner(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new PermissionDeniedException("Нельзя менять статус не владельцу.");
        }

        BookingStatus bookingStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(bookingStatus);
        bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional
    public BookingResponseDto getBooking(Long requestUserId, Long id) {

        Booking booking = getBookingById(id);

        if (!requestUserId.equals(booking.getBooker().getId()) && !requestUserId.equals(booking.getItem().getOwner().getId())) {
            throw new PermissionDeniedException("Бронирование может получить либо владелец, либо тот, кто создал бронь");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking id = " + id + " не найдена"));
    }

    @Transactional
    public List<BookingResponseDto> getBookingsByBooker(Long requestUserId, String state) {

        userService.getUserById(requestUserId);

        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case "ALL" -> bookingRepository.findAllByBooker_IdOrderByStartDesc(requestUserId);
            case "CURRENT" ->
                    bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(requestUserId, currentTime, currentTime);
            case "PAST" -> bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(requestUserId, currentTime);
            case "FUTURE" ->
                    bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(requestUserId, currentTime);
            //            case "WAITING" -> null;
            //            case "REJECTED" -> null;
            default -> bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(requestUserId, state);
        };

        return BookingMapper.mapToBookingDto(bookings);
    }

    @Transactional
    public List<BookingResponseDto> getBookingsByOwner(Long requestUserId, String state) {

        userService.getUserById(requestUserId);

        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case "ALL" -> bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(requestUserId);
            case "CURRENT" ->
                    bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(requestUserId, currentTime, currentTime);
            case "PAST" ->
                    bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(requestUserId, currentTime);
            case "FUTURE" ->
                    bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(requestUserId, currentTime);
            //            case "WAITING" -> null;
            //            case "REJECTED" -> null;
            default -> bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(requestUserId, state);
        };

        return BookingMapper.mapToBookingDto(bookings);
    }

    public void validateUserBookedItem(Long userId, Long itemId) {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Booking> userBookings = bookingRepository.findAllByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId,
                userId,
                BookingStatus.APPROVED,
                currentTime
        );

        if (userBookings.isEmpty()) {
            throw new InvalidDataException("Пользователь ID " + userId + " не бронировал вещь ID " + itemId);
        }
    }

    public BookingInItemDto getLastBooking(
            Long itemId,
            BookingStatus status,
            LocalDateTime currentTime
    ) {
        return BookingMapper.mapToBookingInItemDto(
                bookingRepository.findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(
                        itemId,
                        status,
                        currentTime
                )
        );
    }


    public BookingInItemDto getNextBooking(
            Long itemId,
            BookingStatus status,
            LocalDateTime currentTime
    ) {
        return BookingMapper.mapToBookingInItemDto(
                bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                        itemId,
                        status,
                        currentTime
                )
        );
    }

}
