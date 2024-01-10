package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;

import javax.validation.Valid;
import java.util.Collections;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    /**
     * Добавление вещи
     *
     * @param ownerId        id собственника
     * @param createdItemDto createdItemDto
     * @return ItemDto
     */

    @PostMapping
    ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                   @Valid @RequestBody CreatedItemDto createdItemDto) {
        log.debug("Добавлен новый пользователь {}", createdItemDto);
        return itemClient.createItem(ownerId, createdItemDto);
    }

    /**
     * Обновление данных о вещи
     *
     * @param ownerId        id собственника
     * @param updatedItemDto updatedItemDto
     * @return ItemDto
     */

    @PatchMapping(path = "/{itemId}")
    ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable long itemId,
                                      @Valid @RequestBody UpdatedItemDto updatedItemDto) {
        if (itemId <= 0) {
            throw new NotFoundException("Id вещи должен быть положительным числом");
        }
        log.debug("Обновлена вещь {}", updatedItemDto);
        return itemClient.updateItem(ownerId, itemId, updatedItemDto);
    }

    /**
     * Поиск вещи по id
     *
     * @param userId id пользователя, делающего запрос
     * @param itemId id вещи
     * @return ItemDto
     */

    @GetMapping(path = "/{itemId}")
    ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        if (itemId <= 0) {
            throw new NotFoundException("Id вещи должен быть положительным числом");
        }
        return itemClient.getItemById(userId, itemId);
    }

    /**
     * Поиск вещи по id
     *
     * @param ownerId id собственника
     * @return List<ItemDto>
     */

    @GetMapping
    ResponseEntity<Object> getItemListByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(required = false) Long from,
                                               @RequestParam(required = false) Long size) {
        if (from != null && from < 0) {
            throw new IllegalStateException("Индекс запроса не может быть меньше нуля");
        }
        if (size != null && size < 1) {
            throw new IllegalStateException("Размер списка не может быть меньше 1");
        }
        log.debug("Получен искомый список");
        return itemClient.getItemListByUserId(ownerId, from, size);
    }

    /**
     * Поиск вещи по имени/описанию
     *
     * @param text искомы текст
     * @return List<ItemDto>
     */

    @GetMapping("/search")
    ResponseEntity<Object> searchItemsByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam String text,
                                             @RequestParam(required = false) Long from,
                                             @RequestParam(required = false) Long size) {
        if (text.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        if (from != null && from < 0) {
            throw new IllegalStateException("Индекс запроса не может быть меньше нуля");
        }
        if (size != null && size < 1) {
            throw new IllegalStateException("Размер списка не может быть меньше 1");
        }
        log.debug("Получен искомый список");
        return itemClient.searchItemsByText(userId, text, from, size);
    }

    /**
     * Добавление комментария
     *
     * @param userId            id комментатора
     * @param itemId            id вещи
     * @param createdCommentDto createdCommentDto
     * @return CommentDto
     */

    @PostMapping(path = "/{itemId}/comment")
    ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                      @Valid @RequestBody CreatedCommentDto createdCommentDto) {
        if (itemId == null) {
            throw new NotFoundException("Не указан id вещи, к которой добавляется отзыв");
        }
        log.debug("Добавлен новый комментарий {} от пользователя {}", createdCommentDto, userId);
        return itemClient.addComment(userId, itemId, createdCommentDto);
    }
}
