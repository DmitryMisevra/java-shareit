package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class CreatedBookingDtoJsonTest {

    @Autowired
    private JacksonTester<CreatedBookingDto> json;

    @Test
    public void testCreatedBookingDtoDeserialization() throws Exception {
        String content = "{\"itemId\": 1, \"start\": \"2023-12-10T10:00:00\", \"end\": \"2023-12-12T10:00:00\"}";
        CreatedBookingDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 10, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 12, 10, 0));
    }
}
