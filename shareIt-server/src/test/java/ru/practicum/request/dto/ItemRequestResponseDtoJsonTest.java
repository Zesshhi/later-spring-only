package ru.practicum.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    @Test
    void serializeItemRequestResponseDto() throws Exception {
        ItemRequestResponseDto dto = new ItemRequestResponseDto(
                1L,
                "Need a drill",
                LocalDateTime.of(2024, 1, 1, 10, 15, 30),
                List.of(new ItemRequestAnswerDto(2L, "Drill", 3L, 1L))
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:15:30");
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Drill");
    }
}
