package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreatedItemRequestDto передается при создании запроса
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatedItemRequestDto {

    private String description;
}
