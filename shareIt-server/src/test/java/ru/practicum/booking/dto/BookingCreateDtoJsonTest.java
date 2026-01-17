package ru.practicum.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void serializeBookingCreateDto() throws Exception {
        BookingCreateDto dto = new BookingCreateDto(
                1L,
                5L,
                LocalDateTime.of(2024, 2, 1, 12, 0),
                LocalDateTime.of(2024, 2, 2, 12, 0)
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-02-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-02-02T12:00:00");
    }
}
