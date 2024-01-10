package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * CreatedItemDto передается при создании вещи
 */

@Data
@Builder
public class CreatedItemDto {

    private final String name;
    private final String description;
    private final Boolean available;
    private Long requestId;
}
