package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;

    private CreatedItemRequestDto createdItemRequestDto;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestMapper = new ItemRequestMapper();

        createdItemRequestDto = CreatedItemRequestDto.builder()
                .description("test description")
                .build();

        User requestor = User.builder()
                .id(2L)
                .name("user")
                .email("user@user.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test name")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(1L)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .items(Collections.singletonList(itemDto))
                .build();


    }

    @Test
    void createdItemRequestDtoToItemRequest_whenMappingValidCreatedRequestDto_thenReturnItemRequest() {
        ItemRequest convertedItemRequest = itemRequestMapper.createdItemRequestDtoToItemRequest(createdItemRequestDto);

        assertNotNull(convertedItemRequest);
        assertEquals(createdItemRequestDto.getDescription(), convertedItemRequest.getDescription());
        assertNull(convertedItemRequest.getId());
        assertNull(convertedItemRequest.getRequestor());
        assertNull(convertedItemRequest.getCreated());
        assertNull(convertedItemRequest.getItems());

    }

    @Test
    void itemRequestToItemRequestDto_whenMappingValidCreatedRequest_thenReturnItemRequestDto() {
        ItemRequestDto convertedItemRequestDto = itemRequestMapper.itemRequestToItemRequestDto(itemRequest);

        assertNotNull(convertedItemRequestDto);
        assertEquals(itemRequest.getId(), convertedItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), convertedItemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), convertedItemRequestDto.getCreated());
        assertEquals(itemRequest.getItems(), convertedItemRequestDto.getItems());
    }
}
