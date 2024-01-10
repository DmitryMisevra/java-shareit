package ru.practicum.shareit.request.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    CreatedItemRequestDto createdItemRequestDto = CreatedItemRequestDto.builder()
            .description("Test description")
            .build();

    CreatedItemRequestDto createdItemRequestDtoInvalidDescription = CreatedItemRequestDto.builder()
            .description(null)
            .build();

    ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("Test description")
            .created(LocalDateTime.now())
            .build();

    ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
            .id(2L)
            .description("Test description2")
            .created(LocalDateTime.now())
            .build();


    @SneakyThrows
    @Test
    public void addItemRequest_whenValidRequest_thenReturnStatusOkWithItemRequestDto() {
        Long validUserId = 1L;

        when(itemRequestService.addItemRequest(anyLong(), any(CreatedItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", validUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    public void addItemRequest_whenInvalidUserId_thenReturnBadRequest() {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getItemRequestListByUserId_whenValidRequest_thenReturnStatusOkWithListOfItemRequestDto() {
        Long validOwnerId = 1L;
        List<ItemRequestDto> itemRequestList = Arrays.asList(itemRequestDto, itemRequestDto2);

        when(itemRequestService.getItemRequestListByUserId(validOwnerId))
                .thenReturn(itemRequestList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", validOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestList)));
    }

    @SneakyThrows
    @Test
    public void getItemRequestListByUserId_whenInvalidUserId_thenReturnBadRequest() {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getAllItemRequestList_whenValidRequest_thenReturnStatusOkWithListOfItemRequestDto() {
        Long validUserId = 1L;
        Long from = 0L;
        Long size = 10L;
        List<ItemRequestDto> itemRequestList = Arrays.asList(itemRequestDto, itemRequestDto2);

        when(itemRequestService.getAllItemRequestList(validUserId, from, size))
                .thenReturn(itemRequestList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", validUserId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestList)));
    }

    @SneakyThrows
    @Test
    public void getRequestByItemRequestId_whenValidRequest_thenReturnStatusOkWithItemRequestDto() {
        Long validUserId = 1L;
        Long validRequestId = 1L;

        when(itemRequestService.getRequestByItemRequestId(validUserId, validRequestId))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", validRequestId)
                        .header("X-Sharer-User-Id", validUserId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    public void getRequestByItemRequestId_whenNoUserId_thenReturnBadRequest() {
        Long validRequestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", validRequestId))
                .andExpect(status().isBadRequest());
    }
}


