package ru.practicum.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

/**
 * BookingMapper для маппинга Dto
 */

@Component
@AllArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

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
                .item(itemMapper.itemToItemDto(booking.getItem()))
                .booker(userMapper.userToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }
}
