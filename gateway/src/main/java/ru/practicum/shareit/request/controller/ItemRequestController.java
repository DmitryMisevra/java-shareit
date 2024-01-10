package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    /**
     * Добавление запроса на вещь
     *
     * @param userId                id пользователя
     * @param createdItemRequestDto createdItemRequestDto
     * @return ItemRequestDto
     */

    @PostMapping
    ResponseEntity<Object> addItemRequest(
            @RequestHeader("X-Sharer-User-Id") @NotNull(message = "Не указан id пользователя") Long userId,
            @Valid @RequestBody CreatedItemRequestDto createdItemRequestDto
    ) {
        ResponseEntity<Object> responseEntity = itemRequestClient.addItemRequest(userId, createdItemRequestDto);
        String responseBodyAsString = (responseEntity.getBody() != null) ? responseEntity.getBody().toString() : "null";
        log.debug("Добавлен новый запрос с id={}", responseBodyAsString);
        return responseEntity;
    }

    /**
     * Получить список всех запросов определенного пользователя
     *
     * @param ownerId id пользователя, оставившего запрос
     * @return List<ItemRequestDto>
     */

    @GetMapping
    ResponseEntity<Object> getItemRequestListByUserId(
            @RequestHeader("X-Sharer-User-Id") @NotNull(message = "Не указан id пользователя") Long ownerId
    ) {
        ResponseEntity<Object> responseEntity = itemRequestClient.getItemRequestListByUserId(ownerId);
        String responseBodyAsString = (responseEntity.getBody() != null) ? responseEntity.getBody().toString() : "null";
        log.debug("Получен список запросов по id={}, список={}", ownerId, responseBodyAsString);
        return responseEntity;
    }

    /**
     * Получить список всех запросов, на которые может ответить пользователь
     *
     * @param userId id пользователя
     * @param from   индекс, с которого начинается список
     * @param size   размер списка
     * @return List<ItemRequestDto>
     */

    @GetMapping("/all")
    ResponseEntity<Object> getAllItemRequestList(
            @RequestHeader("X-Sharer-User-Id") @NotNull(message = "Не указан id пользователя") Long userId,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long size
    ) {
        if (from != null && from < 0) {
            throw new IllegalStateException("Индекс запроса не может быть меньше нуля");
        }
        if (size != null && size < 1) {
            throw new IllegalStateException("Размер списка не может быть меньше 1");
        }
        ResponseEntity<Object> responseEntity = itemRequestClient.getAllItemRequestList(userId, from, size);
        String responseBodyAsString = (responseEntity.getBody() != null) ? responseEntity.getBody().toString() : "null";
        log.debug("Получен список всех запросов с from={}, size={}, список={}", from, size, responseBodyAsString);
        return responseEntity;
    }

    /**
     * Получение информации о запросе по его id
     *
     * @param userId    id пользователя
     * @param requestId id запроса
     * @return ItemRequestDto
     */

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequestByItemRequestId(
            @RequestHeader("X-Sharer-User-Id") @NotNull(message = "Не указан id пользователя") Long userId,
            @PathVariable @NotNull(message = "Не указан id запроса") Long requestId
    ) {
        ResponseEntity<Object> responseEntity = itemRequestClient.getRequestByItemRequestId(userId, requestId);
        String responseBodyAsString = (responseEntity.getBody() != null) ? responseEntity.getBody().toString() : "null";
        log.debug("Найден запрос с id={}, запрос={}", userId, responseBodyAsString);
        return responseEntity;
    }
}
