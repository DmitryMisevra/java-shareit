package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CreatedBookingDto передается при создании вещи
 */

@Data
@Builder
public class CreatedBookingDto {

    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
