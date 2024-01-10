package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    CreatedItemDto createdItemDto = CreatedItemDto.builder()
            .name("Дрель")
            .description("Электрическая дрель")
            .available(true)
            .build();

    UpdatedItemDto updatedItemDto = UpdatedItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .available(false)
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .build();

    ItemDto itemDtoUpd = ItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .ownerId(1L)
            .available(false)
            .build();

    CreatedCommentDto createdCommentDto = CreatedCommentDto.builder()
            .text("test comment")
            .build();

    CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("test comment")
            .authorName("user")
            .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
            .build();


    @SneakyThrows
    @Test
    void addItem_whenAddItem_thenReturnStatusOkWithItemDtoInBody() {
        Long ownerId = 1L;

        when(itemClient.createItem(eq(ownerId), any(CreatedItemDto.class)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @SneakyThrows
    @Test
    void addItem_whenAddItemWithoutOwnerId_thenResponseStatusBadRequest() {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createdItemDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItem_whenValidInput_thenReturnResponseEntityOkWithItemDtoUpdInBody() {
        Long ownerId = 1L;
        long itemId = 1L;

        when(itemClient.updateItem(eq(ownerId), eq(itemId), any(UpdatedItemDto.class)))
                .thenReturn(new ResponseEntity<>(itemDtoUpd, HttpStatus.OK));

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoUpd.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoUpd.getName()));
    }

    @SneakyThrows
    @Test
    void updateItem_whenInvalidItemId_thenThrowsNotFoundException() {
        Long ownerId = 1L;
        long invalidItemId = -1L;

        mockMvc.perform(patch("/items/{id}", invalidItemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isNotFound());
    }


    @SneakyThrows
    @Test
    void getItemById_whenValidInput_thenReturnResponseStatusOkWitheItemDtoInBody() {
        Long userId = 1L;
        long itemId = 1L;

        when(itemClient.getItemById(eq(userId), eq(itemId)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @SneakyThrows
    @Test
    void getItemById_whenInvalidItemId_thenThrowsNotFoundException() {
        Long userId = 1L;
        long invalidItemId = -1L;

        mockMvc.perform(get("/items/{id}", invalidItemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemListByUserId_whenValidInput_thenReturnStatusOkWithItemListInBody() throws Exception {
        Long ownerId = 1L;

        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Электрическая дрель")
                .ownerId(1L)
                .available(true)
                .build();


        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Молоток")
                .description("Молоток для забивания гвоздей")
                .ownerId(1L)
                .available(true)
                .build();

        List<ItemDto> itemDtoList = Arrays.asList(itemDto1, itemDto2);

        when(itemClient.getItemListByUserId(eq(ownerId), any(), any()))
                .thenReturn(new ResponseEntity<>(itemDtoList, HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(itemDtoList.size()))
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()));
    }

    @SneakyThrows
    @Test
    void getItemListByUserId_whenInvalidFrom_thenThrowsException() {
        Long ownerId = 1L;
        Long invalidFrom = -1L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", invalidFrom.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getItemListByUserId_whenInvalidSize_thenThrowsException() {
        Long ownerId = 1L;
        Long invalidSize = 0L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("size", invalidSize.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchItemsByText_whenValidInput_thenReturnStatusOkWithItemListInBody() {
        String searchText = "Дрель";
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Электрическая дрель")
                .ownerId(1L)
                .available(true)
                .build();


        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Молоток")
                .description("Молоток для забивания гвоздей")
                .ownerId(1L)
                .available(true)
                .build();

        List<ItemDto> itemDtoList = Arrays.asList(itemDto1, itemDto2);

        when(itemClient.searchItemsByText(anyLong(), eq(searchText), any(), any()))
                .thenReturn(new ResponseEntity<>(itemDtoList, HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", searchText)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(itemDtoList.size()))
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()));
    }

    @SneakyThrows
    @Test
    void searchItemsByText_whenEmptyText_thenReturnsStatusOkWithEmptyList() {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @SneakyThrows
    @Test
    void searchItemsByText_whenInvalidFrom_thenThrowsException() {
        String searchText = "Дрель";
        Long invalidFrom = -1L;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", searchText)
                        .param("from", invalidFrom.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void searchItemsByText_whenInvalidSize_thenThrowsException() {
        String searchText = "Дрель";
        Long invalidSize = 0L;

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .param("size", invalidSize.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @Test
    void addComment_whenValidInput_thenReturnStatusOkWithCommentDtoInBody() {
        Long userId = 1L;
        Long itemId = 1L;

        when(itemClient.addComment(eq(userId), eq(itemId), any(CreatedCommentDto.class)))
                .thenReturn(new ResponseEntity<>(commentDto, HttpStatus.OK));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }

    @SneakyThrows
    @Test
    void addComment_whenInvalidItemId_thenThrowsException() {
        Long userId = 1L;
        Long invalidItemId = null;

        mockMvc.perform(post("/items/{itemId}/comment", invalidItemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdCommentDto)))
                .andExpect(status().isNotFound());
    }
}
