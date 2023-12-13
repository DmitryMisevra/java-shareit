package ru.practicum.shareit.booking.mapper;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;

/**
 * BookingMapper для маппинга Dto
 */

@Component
public class BookingMapper {

    public Booking createdBookingDtoToBooking(@NonNull CreatedBookingDto createdBookingDto) {
        return Booking.builder()
                .start(createdBookingDto.getStart())
                .end(createdBookingDto.getEnd())
                .build();
    }

    public BookingDto bookingToBookingDto(@NonNull Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }
}
