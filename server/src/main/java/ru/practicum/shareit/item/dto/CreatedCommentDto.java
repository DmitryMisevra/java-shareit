package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreatedCommentDto передается при создании отзыва
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class CreatedCommentDto {

    private String text;
}
