package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * BookingInfoDto передается для статусов nextBooking и lastBooking в BookingDto
 */

@Data
@Builder
public class BookingDto {

    @NotNull
    @Positive(message = "Id брони должен быть положительным числом")
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private ItemDto item;
    @NotNull
    private UserDto booker;
    @NotNull
    private Status status;
}
