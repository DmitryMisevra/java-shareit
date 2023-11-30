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
        ItemDto itemDto = itemService.updateItem(ownerId, itemId, updatedItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        log.debug("Обновлена вещь с id={}", itemDto.getId());
        return ResponseEntity.ok(itemDto);
    }

    /**
     * Поиск вещи по id
     *
     * @param id id вещи
     * @return ItemDto
     */

    @GetMapping(path = "/{id}")
    ResponseEntity<ItemDto> getItemById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id вещи должен быть положительным числом");
        }
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id:" + id));
    }

    /**
     * Поиск вещи по id
     *
     * @param ownerId id собственника
     * @return List<ItemDto>
     */

    @GetMapping
    ResponseEntity<List<ItemDto>> getItemListByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        if (ownerId == null) {
            throw new NotFoundException("Не указан id собственника вещи");
        }
        return ResponseEntity.ok(itemService.getItemListByUserId(ownerId));
    }

    /**
     * Поиск вещи по имени/описанию
     *
     * @param text искомы текст
     * @return List<ItemDto>
     */

    @GetMapping("/search")
    ResponseEntity<List<ItemDto>> searchItemsByText(@RequestParam String text) {
        if (text.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(itemService.searchItemsByText(text));
    }
}
