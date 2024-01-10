package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * UpdatedItemDto передается при обновлении вещи
 */

@Data
@Builder
public class UpdatedItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
