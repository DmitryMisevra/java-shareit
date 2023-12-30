package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTest {

    private ItemMapper itemMapper;

    private Item item;
    private CreatedItemDto createdItemDto;
    private UpdatedItemDto updatedItemDto;

    @BeforeEach
    public void setUp() {
        itemMapper = new ItemMapper();

        createdItemDto = CreatedItemDto.builder()
                .name("Дрель")
                .description("Электрическая дрель")
                .available(true)
                .build();

        updatedItemDto = UpdatedItemDto.builder()
                .id(1L)
                .name("Дрель+")
                .description("Аккумуляторная дрель")
                .available(false)
                .build();

        BookingInfoDto lastBooking = BookingInfoDto.builder()
                .id(1L)
                .bookerId(3L)
                .build();

        BookingInfoDto nextBooking = BookingInfoDto.builder()
                .id(2L)
                .bookerId(4L)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName("testAuthorName1")
                .text("test comment text1")
                .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
                .build();

        item = Item.builder()
                .id(1L)
                .name("test name")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(1L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(Collections.singletonList(commentDto))
                .build();


    }

    @Test
    void createdItemDtoToItem_whenMappingValidCreatedItemDto_thenReturnItem() {
        Item convertedItem = itemMapper.createdItemDtoToItem(createdItemDto);

        assertNotNull(convertedItem);
        assertEquals(createdItemDto.getName(), convertedItem.getName());
        assertEquals(createdItemDto.getDescription(), convertedItem.getDescription());
        assertNull(convertedItem.getId());
    }

    @Test
    void updatedItemDtoToItem_whenMappingValidUpdatedItemDto_thenReturnItem() {
        Item convertedItem = itemMapper.updatedItemDtoToItem(updatedItemDto);

        assertNotNull(convertedItem);
        assertEquals(updatedItemDto.getName(), convertedItem.getName());
        assertEquals(updatedItemDto.getDescription(), convertedItem.getDescription());
        assertNull(convertedItem.getId());
    }

    @Test
    void itemToItemDto_whenMappingValidItem_thenReturnItemDto() {
        ItemDto convertedItemDto = itemMapper.itemToItemDto(item);

        assertNotNull(convertedItemDto);
        assertEquals(item.getId(), convertedItemDto.getId());
        assertEquals(item.getName(), convertedItemDto.getName());
        assertEquals(item.getDescription(), convertedItemDto.getDescription());
        assertEquals(item.getOwnerId(), convertedItemDto.getOwnerId());
        assertEquals(item.getLastBooking(), convertedItemDto.getLastBooking());
        assertEquals(item.getNextBooking(), convertedItemDto.getNextBooking());
        assertEquals(item.getComments().get(0), convertedItemDto.getComments().get(0));
    }


}
