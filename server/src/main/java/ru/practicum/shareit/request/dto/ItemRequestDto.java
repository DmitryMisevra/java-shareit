package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ItemRequestDto передается в http-ответе для всех методов ItemRequestController
 */

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private List<ItemDto> items;
}
