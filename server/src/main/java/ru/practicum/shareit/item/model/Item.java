package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Базовая сущность Item
 */

@Entity
@Table(name = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Long id;
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(name = "item_description", nullable = false)
    private String description;
    @Column(name = "item_ownerid", nullable = false)
    private Long ownerId;
    @Column(name = "item_available", nullable = false)
    private Boolean available;
    @Column(name = "request_id")
    private Long requestId;
    @Transient
    private BookingInfoDto lastBooking;
    @Transient
    private BookingInfoDto nextBooking;
    @Transient
    private List<CommentDto> comments;

    /**
     * Обновление Item всех непустых полей
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
