package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor

public class BookingInfoDto {

    @NotNull
    @Positive(message = "Id брони должен быть положительным числом")
    private Long id;
    @NotNull
    @Positive(message = "Id пользователя, разместившего бронь должен быть положительным числом")
    private Long bookerId;
}
