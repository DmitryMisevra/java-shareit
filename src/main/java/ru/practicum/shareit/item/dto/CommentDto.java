package ru.practicum.shareit.item.dto;

/**
 * CommentDto передается в http-ответе для всех метода addComment
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {

    @Positive(message = "Id комментария должен быть положительным числом")
    private final Long id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 200, message = "Размер описания не может превышать 255 символов")
    private final String text;
    @NotBlank(message = "Имя автора не может быть пустым")
    private final String authorName;
    @NotNull
    private LocalDateTime created;
}
