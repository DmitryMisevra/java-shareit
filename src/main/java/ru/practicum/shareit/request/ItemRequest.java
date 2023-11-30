package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * Базовая сущность ItemRequest
 */

@Data
@Builder
public class ItemRequest {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    public ItemRequest copyOf() {
        return ItemRequest.builder()
                .id(this.id)
                .description(this.description)
                .requestor(this.requestor)
                .created(this.created)
                .build();
    }
}
