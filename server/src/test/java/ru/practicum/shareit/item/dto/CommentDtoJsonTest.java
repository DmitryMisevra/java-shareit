package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    public void testCommentDtoSerialization() throws Exception {
        LocalDateTime createdTime = LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30);

        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("test comment")
                .authorName("user")
                .created(createdTime)
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("test comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-12-10T15:30:00");
    }
}
