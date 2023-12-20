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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Добавление вещи
     *
     * @param ownerId        id собственника
     * @param createdItemDto createdItemDto
     * @return ItemDto
     */

    @PostMapping
    ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @Valid @RequestBody CreatedItemDto createdItemDto) {
        if (ownerId == null) {
            throw new NotFoundException("Не указан id собственника вещи");
        }
        ItemDto itemDto = itemService.createItem(ownerId, createdItemDto);
        log.debug("Добавлен новый пользователь с id={}", itemDto.getId());
        return ResponseEntity.ok(itemDto);
    }

    /**
     * Обновление данных о вещи
     *
     * @param ownerId        id собственника
     * @param updatedItemDto updatedItemDto
     * @return ItemDto
     */

    @PatchMapping(path = "/{itemId}")
    ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable long itemId,
                                       @Valid @RequestBody UpdatedItemDto updatedItemDto) {
        if (ownerId == null) {
            throw new NotFoundException("Не указан id собственника вещи");
        }
        if (itemId <= 0) {
            throw new NotFoundException("Id вещи должен быть положительным числом");
        }
        ItemDto itemDto = itemService.updateItem(ownerId, itemId, updatedItemDto);
        log.debug("Обновлена вещь с id={}", itemDto.getId());
        return ResponseEntity.ok(itemDto);
    }

    /**
     * Поиск вещи по id
     *
     * @param userId id пользователя, делающего запрос
     * @param itemId id вещи
     * @return ItemDto
     */

    @GetMapping(path = "/{itemId}")
    ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя, запрашивающего информацию о вещи");
        }
        if (itemId <= 0) {
            throw new NotFoundException("Id вещи должен быть положительным числом");
        }
        return ResponseEntity.ok(itemService.getItemById(userId, itemId));
    }

    /**
     * Поиск вещи по id
     *
     * @param ownerId id собственника
     * @return List<ItemDto>
     */

    @GetMapping
    ResponseEntity<List<ItemDto>> getItemListByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @RequestParam(required = false) Long from,
                                                      @RequestParam(required = false) Long size) {
        if (ownerId == null) {
            throw new NotFoundException("Не указан id собственника вещи");
        }
        if (from != null && from < 0) {
            throw new IllegalStateException("Индекс запроса не может быть меньше нуля");
        }
        if (size != null && size < 1) {
            throw new IllegalStateException("Размер списка не может быть меньше 1");
        }
        List<ItemDto> itemDtoList = itemService.getItemListByUserId(ownerId, from, size);
        log.debug("Получен искомый список {}", itemDtoList);
        return ResponseEntity.ok(itemDtoList);
    }

    /**
     * Поиск вещи по имени/описанию
     *
     * @param text искомы текст
     * @return List<ItemDto>
     */

    @GetMapping("/search")
    ResponseEntity<List<ItemDto>> searchItemsByText(@RequestParam String text,
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
        List<ItemDto> itemDtoList = itemService.searchItemsByText(text, from, size);
        log.debug("Получен искомый список {}", itemDtoList);
        return ResponseEntity.ok(itemDtoList);
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
    ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                          @Valid @RequestBody CreatedCommentDto createdCommentDto) {
        if (userId == null) {
            throw new NotFoundException("Не указан id комментатора");
        }
        if (itemId == null) {
            throw new NotFoundException("Не указан id вещи, к которой добавляется отзыв");
        }
        CommentDto commentDto = itemService.addComment(userId, itemId, createdCommentDto);
        log.debug("Добавлен новый комментарий с id={} от пользователя {}", commentDto.getId(),
                commentDto.getAuthorName());
        return ResponseEntity.ok(commentDto);
    }
}
