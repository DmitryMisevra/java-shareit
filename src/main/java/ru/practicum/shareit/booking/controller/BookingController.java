package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Добавление букинга
     *
     * @param creatorId       id создателя
     * @param createdBookingDto createdBookingDto
     * @return BookingDto
     */

    @PostMapping
    ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long creatorId, @Valid @RequestBody
    CreatedBookingDto createdBookingDto) {
        if (creatorId == null) {
            throw new NotFoundException("Не указан id пользователя, который создает запрос на бронь");
        }
        BookingDto bookingDto = bookingService.createBooking(creatorId, createdBookingDto);
        log.debug("Добавлена новая бронь с id={}", bookingDto.getId());
        return ResponseEntity.ok(bookingDto);
    }

    /**
     * Обновление статуса букинга
     *
     * @param userId       id пользователя, который хочет изменить статус
     * @param bookingId    id букинга, статус которого нужно изменить
     * @return BookingDto
     */

    @PatchMapping(path = "/{bookingId}")
    ResponseEntity<BookingDto> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long bookingId, @RequestParam Boolean approved) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя, который создает запрос на бронь");
        }
        if (bookingId == null) {
            throw new NotFoundException("Не указан id брони");
        }
        BookingDto bookingDto = bookingService.updateBookingStatus(userId, bookingId, approved);
        log.debug("Обновлен статус брони с id={}", bookingDto.getId());
        return ResponseEntity.ok(bookingDto);
    }

    /**
     * Поиск букинга по id
     *
     * @param userId       id пользователя запрашивающего букинг
     * @param bookingId    id букинга
     * @return BookingDto
     */

    @GetMapping(path = "/{bookingId}")
    ResponseEntity<BookingDto> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя");
        }
        if (bookingId == null) {
            throw new NotFoundException("Не указан id брони");
        }
        return ResponseEntity.ok(bookingService.findBookingById(userId, bookingId));
    }

    /**
     * Список букингов, созданных пользователем
     *
     * @param userId       id пользователя запрашивающего букинг
     * @param state        статус, по которому будут отфильтрованы букинги
     * @return List<BookingDto>
     */

    @GetMapping
    ResponseEntity<List<BookingDto>> getBookingListCreatedByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(defaultValue = "ALL") String state) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя");
        }
        List<BookingDto> bookingList = bookingService.getBookingListCreatedByUserId(userId, state);
        return ResponseEntity.ok(bookingList);
    }

    /**
     * Список всех букингов для вещей пользователя
     *
     * @param userId       id пользователя запрашивающего букинг
     * @param state        статус, по которому будут отфильтрованы букинги
     * @return List<BookingDto>
     */

    @GetMapping("/owner")
    ResponseEntity<List<BookingDto>> getBookingListForAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                    @RequestParam(defaultValue = "ALL") String state) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя");
        }
        return ResponseEntity.ok(bookingService.getBookingListForAllOwnerItems(userId, state));
    }
}
