package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .build();

    @Test
    public void testItemRequestDtoSerialization() throws Exception {
        LocalDateTime createdTime = LocalDateTime.now();
        List<ItemDto> itemList = Collections.singletonList(itemDto);

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Тестовое описание")
                .created(createdTime)
                .items(itemList)
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Тестовое описание");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(createdTime.toString());

        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("Электрическая дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
    }
}
