package ru.practicum.shareit.item.dto;

/**
 * CommentDto передается в http-ответе для всех метода addComment
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {

    private final Long id;
    private final String text;
    private final String authorName;
    private LocalDateTime created;
}
