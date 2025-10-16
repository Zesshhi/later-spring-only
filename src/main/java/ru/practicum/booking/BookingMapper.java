package ru.practicum.booking;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStartDate(), booking.getEndDate(), booking.getItemId(), booking.isApproved());
    }

    public static List<BookingDto> mapToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingDtos = bookings.stream().map(BookingMapper::mapToBookingDto).toList();
        return bookingDtos;
    }

    public static Booking mapToNewBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStartDate(bookingDto.getStartDate());
        booking.setEndDate(bookingDto.getEndDate());
        booking.setItemId(bookingDto.getItemId());
        booking.setApproved(bookingDto.isApproved());

        return booking;
    }
}
