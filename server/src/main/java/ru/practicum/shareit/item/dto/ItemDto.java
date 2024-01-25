package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

/**
 * itemDto передается в http-ответе для всех методов ItemController
 */

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {

    private final Long id;
    private final String name;
    private final String description;
    private final Long ownerId;
    private final Boolean available;
    private final Long requestId;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;
}
