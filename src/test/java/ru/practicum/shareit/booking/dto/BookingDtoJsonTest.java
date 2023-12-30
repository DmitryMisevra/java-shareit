package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    User booker = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    Item item = Item.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(2L)
            .available(true)
            .requestId(1L)
            .build();

    @Test
    public void testBookingDtoSerialization() throws Exception {

        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 12, 0))
                .end(LocalDateTime.of(2023, 1, 2, 12, 0))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-02T12:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("Электрическая дрель");
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId").isEqualTo(2);
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("user@user.com");

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
