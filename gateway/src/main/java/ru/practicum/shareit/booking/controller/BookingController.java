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
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    /**
     * Добавление букинга
     *
     * @param creatorId         id создателя
     * @param createdBookingDto createdBookingDto
     * @return BookingDto
     */

    @PostMapping
    ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long creatorId, @Valid @RequestBody
    CreatedBookingDto createdBookingDto) {
        log.debug("Добавлена новая бронь {}", createdBookingDto);
        return bookingClient.createBooking(creatorId, createdBookingDto);
    }

    /**
     * Обновление статуса букинга
     *
     * @param userId    id пользователя, который хочет изменить статус
     * @param bookingId id букинга, статус которого нужно изменить
     * @return BookingDto
     */

    @PatchMapping(path = "/{bookingId}")
    ResponseEntity<Object> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.debug("Обновлен статус брони с id={}", bookingId);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    /**
     * Поиск букинга по id
     *
     * @param userId    id пользователя запрашивающего букинг
     * @param bookingId id букинга
     * @return BookingDto
     */

    @GetMapping(path = "/{bookingId}")
    ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId) {
        return bookingClient.findBookingById(userId, bookingId);
    }

    /**
     * Список букингов, созданных пользователем
     *
     * @param userId id пользователя запрашивающего букинг
     * @param state  статус, по которому будут отфильтрованы букинги
     * @return List<BookingDto>
     */

    @GetMapping
    ResponseEntity<Object> getBookingListCreatedByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(defaultValue = "ALL") String state,
                                                                   @RequestParam(required = false) Long from,
                                                                   @RequestParam(required = false) Long size) {
        if (from != null && from < 0) {
            throw new IllegalStateException("Индекс запроса не может меньше нуля");
        }
        if (size != null && size < 1) {
            throw new IllegalStateException("Размер списка не может быть меньше 1");
        }
        return bookingClient.getBookingListCreatedByUserId(userId, state, from, size);
    }

    /**
     * Список всех букингов для вещей пользователя
     *
     * @param userId id пользователя запрашивающего букинг
     * @param state  статус, по которому будут отфильтрованы букинги
     * @return List<BookingDto>
     */

    @GetMapping("/owner")
    ResponseEntity<Object> getBookingListForAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                    @RequestParam(defaultValue = "ALL") String state,
                                                                    @RequestParam(required = false) Long from,
                                                                    @RequestParam(required = false) Long size) {
        if (from != null && from < 0) {
            throw new IllegalStateException("Индекс запроса не может быть меньше нуля");
        }
        if (size != null && size < 1) {
            throw new IllegalStateException("Размер списка не может быть меньше 1");
        }

        return bookingClient.getBookingListForAllOwnerItems(userId, state, from, size);
    }
}
