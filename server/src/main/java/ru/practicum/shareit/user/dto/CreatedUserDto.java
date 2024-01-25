package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;


/**
 * CreatedUserDto передается при создании пользователя
 */

@Data
@Builder
public class CreatedUserDto {

    private final String name;
    private final String email;
}
