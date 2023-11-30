package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * Базовая сущность Item
 */

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;

    /**
     * Cоздание копии текущего Item
     *
     * @return item
     */

    public Item copyOf() {
        return Item.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .ownerId(this.ownerId)
                .available(this.available)
                .build();
    }

    /**
     * Обновление Item всех непустых полей
     *
     * @return void
     */

    public void updateWith(Item other) {
        if (other.getName() != null) {
            this.name = other.getName();
        }
        if (other.getDescription() != null) {
            this.description = other.getDescription();
        }
        if (other.getAvailable() != null) {
            this.available = other.getAvailable();
        }
    }
}
