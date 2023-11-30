package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Базовая сущность Booking
 */

@Data
@Builder
public class Booking {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String item;
    private String booker;
    private Status status;

    public Booking copyOf() {
        return Booking.builder()
                .id(this.id)
                .start(this.start)
                .end(this.end)
                .booker(this.booker)
                .status(this.status)
                .build();
    }
}


