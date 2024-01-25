package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdatedUserDto передается при обновлении пользователя
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedUserDto {

    private Long id;
    private String name;
    private String email;
}
