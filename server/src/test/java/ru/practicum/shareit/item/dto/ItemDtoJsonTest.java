package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    BookingInfoDto lastBooking = BookingInfoDto.builder()
            .id(1L)
            .bookerId(3L)
            .build();

    BookingInfoDto nextBooking = BookingInfoDto.builder()
            .id(2L)
            .bookerId(4L)
            .build();

    CommentDto comment = CommentDto.builder()
            .id(1L)
            .authorName("testAuthorName1")
            .text("test comment text1")
            .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
            .build();

    @Test
    public void testItemDtoSerialization() throws Exception {
        List<CommentDto> comments = Collections.singletonList(comment);

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Электрическая дрель")
                .ownerId(1L)
                .available(true)
                .requestId(2L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Электрическая дрель");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(4);

        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("testAuthorName1");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("test comment text1");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2023-12-10T15:30:00");
    }
}
