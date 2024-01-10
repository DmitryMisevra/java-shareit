package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long creatorId, CreatedBookingDto createdBookingDto);

    BookingDto updateBookingStatus(long userId, long bookingId, boolean status);

    BookingDto findBookingById(long userId, long bookingId);

    List<BookingDto> getBookingListCreatedByUserId(long userId, String state, Long from, Long size);

    List<BookingDto> getBookingListForAllOwnerItems(long userId, String state, Long from, Long size);


}
