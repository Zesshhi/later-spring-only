package ru.practicum.booking;

import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingCreateDto;
import ru.practicum.booking.dto.BookingInItemDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.item.ItemMapper;
import ru.practicum.user.UserMapper;

import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static BookingResponseDto mapToBookingDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                booking.getStatus(),
                UserMapper.mapToUserDto(booking.getBooker()),
                ItemMapper.mapToItemDto(booking.getItem())
        );
    }

    public static List<BookingResponseDto> mapToBookingDto(List<Booking> bookings) {
        List<BookingResponseDto> bookingDtos = bookings.stream().map(BookingMapper::mapToBookingDto).toList();
        return bookingDtos;
    }

    public static Booking mapToNewBooking(BookingCreateDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        return booking;
    }

    public static BookingInItemDto mapToBookingInItemDto(Booking booking) {
        return new BookingInItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.mapToUserDto(booking.getBooker()),
                ItemMapper.mapToItemDto(booking.getItem())
        );
    }
}
