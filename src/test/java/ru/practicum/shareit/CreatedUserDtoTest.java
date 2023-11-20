package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreatedUserDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializeAndDeserializeTest() throws Exception {
        // Создание и инициализация объекта UserDto
        UserDto originalDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        // Сериализация в JSON
        String json = objectMapper.writeValueAsString(originalDto);

        // Десериализация из JSON обратно в объект
        UserDto deserializedDto = objectMapper.readValue(json, UserDto.class);

        // Проверки
        assertNotNull(deserializedDto);
        assertEquals(originalDto.getId(), deserializedDto.getId());
        assertEquals(originalDto.getName(), deserializedDto.getName());
        assertEquals(originalDto.getEmail(), deserializedDto.getEmail());
    }
}
