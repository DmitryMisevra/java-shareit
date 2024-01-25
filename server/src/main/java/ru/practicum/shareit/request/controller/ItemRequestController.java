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
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    /**
     * Добавление запроса на вещь
     *
     * @param userId                id пользователя
     * @param createdItemRequestDto createdItemRequestDto
     * @return ItemRequestDto
     */

    @PostMapping
    ResponseEntity<ItemRequestDto> addItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CreatedItemRequestDto createdItemRequestDto
    ) {
        ItemRequestDto itemRequestDto = itemRequestService.addItemRequest(userId, createdItemRequestDto);
        log.debug("Добавлен новый запрос с id={}", itemRequestDto.getId());
        return ResponseEntity.ok(itemRequestDto);
    }

    /**
     * Получить список всех запросов определенного пользователя
     *
     * @param ownerId id пользователя, оставившего запрос
     * @return List<ItemRequestDto>
     */

    @GetMapping
    ResponseEntity<List<ItemRequestDto>> getItemRequestListByUserId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        List<ItemRequestDto> itemRequestList = itemRequestService.getItemRequestListByUserId(ownerId);
        return ResponseEntity.ok(itemRequestList);
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
    ResponseEntity<List<ItemRequestDto>> getAllItemRequestList(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long size
    ) {
        List<ItemRequestDto> itemRequestList = itemRequestService.getAllItemRequestList(userId, from, size);
        log.debug("Передан список с from={}, size={}: {}", from, size, itemRequestList);
        return ResponseEntity.ok(itemRequestList);
    }

    /**
     * Получение информации о запросе по его id
     *
     * @param userId    id пользователя
     * @param requestId id запроса
     * @return ItemRequestDto
     */

    @GetMapping("/{requestId}")
    ResponseEntity<ItemRequestDto> getRequestByItemRequestId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        ItemRequestDto itemRequestDto = itemRequestService.getRequestByItemRequestId(userId, requestId);
        log.debug("Найден запрос с id={}", itemRequestDto.getId());
        return ResponseEntity.ok(itemRequestDto);
    }
}
