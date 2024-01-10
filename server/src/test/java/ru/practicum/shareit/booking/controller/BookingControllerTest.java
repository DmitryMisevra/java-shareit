package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .requestId(1L)
            .build();

    CreatedBookingDto createdBookingDto = CreatedBookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .build();

    BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.WAITING)
            .build();

    BookingDto updatedBookingDto = BookingDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.APPROVED)
            .build();

    @SneakyThrows
    @Test
    void createBooking_whenValidRequest_thenReturnStatusOkWithBookingDtoInBody() {
        Long creatorId = 1L;

        when(bookingService.createBooking(eq(creatorId), any(CreatedBookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", creatorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @SneakyThrows
    @Test
    void updateBookingStatus_whenValidRequest_thenReturnUpdatedBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        when(bookingService.updateBookingStatus(eq(userId), eq(bookingId), eq(approved)))
                .thenReturn(updatedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedBookingDto.getId()))
                .andExpect(jsonPath("$.status").value(updatedBookingDto.getStatus().toString()));
    }

    @SneakyThrows
    @Test
    void findBookingById_whenValidRequest_thenReturnBooking() {
        Long userId = 1L;
        Long bookingId = 1L;

        when(bookingService.findBookingById(eq(userId), eq(bookingId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @SneakyThrows
    @Test
    void getBookingListCreatedByUserId_whenValidRequest_thenReturnBookingList() {
        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
                .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
                .status(Status.WAITING)
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.of(2024, Month.FEBRUARY, 15, 15, 30))
                .end(LocalDateTime.of(2024, Month.FEBRUARY, 25, 12, 0))
                .status(Status.WAITING)
                .build();

        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.getBookingListCreatedByUserId(eq(userId), eq(state), eq(from), eq(size)))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingList.size()));
    }

    @SneakyThrows
    @Test
    void getBookingListForAllOwnerItems_whenValidRequest_thenReturnBookingList() {
        Long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;

        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
                .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
                .status(Status.WAITING)
                .build();

        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.of(2024, Month.FEBRUARY, 15, 15, 30))
                .end(LocalDateTime.of(2024, Month.FEBRUARY, 25, 12, 0))
                .status(Status.WAITING)
                .build();

        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);


        when(bookingService.getBookingListForAllOwnerItems(eq(userId), eq(state), eq(from), eq(size)))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingList.size()));
    }
}
