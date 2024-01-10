package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {

    BookingMapper bookingMapper;

    CreatedBookingDto createdBookingDto;

    Booking booking;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper(new UserMapper(), new ItemMapper());

        createdBookingDto = CreatedBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
                .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
                .build();

        User booker = User.builder()
                .id(2L)
                .name("user")
                .email("user@user.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
                .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
                .status(Status.WAITING)
                .build();

    }

    @Test
    void createdBookingDtoToBooking_whenMappingValidBookingDto_thenReturnBooking() {
        Booking convertedBooking = bookingMapper.createdBookingDtoToBooking(createdBookingDto);

        assertNotNull(convertedBooking);
        assertEquals(createdBookingDto.getStart(), convertedBooking.getStart());
        assertEquals(createdBookingDto.getEnd(), convertedBooking.getEnd());
        assertNull(convertedBooking.getId());
        assertNull(convertedBooking.getItem());
        assertNull(convertedBooking.getStatus());
    }

    @Test
    void bookingToBookingDto_whenMappingValidBooking_thenReturnBookingDto() {
        BookingDto convertedBookingDto = bookingMapper.bookingToBookingDto(booking);

        assertNotNull(convertedBookingDto);
        assertEquals(booking.getId(), convertedBookingDto.getId());
        assertEquals(booking.getStart(), convertedBookingDto.getStart());
        assertEquals(booking.getEnd(), convertedBookingDto.getEnd());
        assertEquals(booking.getItem().getId(), convertedBookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), convertedBookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), convertedBookingDto.getStatus());
    }
}
