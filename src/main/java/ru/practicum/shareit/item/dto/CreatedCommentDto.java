package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CreatedCommentDto передается при создании отзыва
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class CreatedCommentDto {
    @NotNull
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 200, message = "Размер описания не может превышать 255 символов")
    private String text;
}
