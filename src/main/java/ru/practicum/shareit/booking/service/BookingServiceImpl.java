package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    @PersistenceContext
    private EntityManager entityManager;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto createBooking(long creatorId, CreatedBookingDto createdBookingDto) {
        User booker = userRepository.findById(creatorId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + creatorId + " не найдена"));
        Item item = itemRepository.findById(createdBookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + createdBookingDto.getItemId() + " не найдена"));

        if (item.getOwnerId() == creatorId) {
            throw new ForbiddenUserException("Вы не можете забронировать вашу вещь");
        }

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь с id: " + item.getId() + " недоступна для брони в данный момент");
        }

        Booking booking = bookingMapper.createdBookingDtoToBooking(createdBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        Booking loadedBooking = bookingRepository.findById(savedBooking.getId())
                .orElseThrow(() -> new IllegalStateException("Ошибка при загрузке Booking. Метод вернул null."));

        return bookingMapper.bookingToBookingDto(loadedBooking);
    }

    @Transactional
    @Override
    public BookingDto updateBookingStatus(long userId, long bookingId, boolean status) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + userId + " не найдена"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id: " + bookingId + " не найдена"));
        Item item = booking.getItem();
        if (userId != item.getOwnerId()) {
            throw new ForbiddenUserException("Изменить статус брони может только владелец вещи");
        }
        Status expectedStatus = status ? Status.APPROVED : Status.REJECTED;
        if (booking.getStatus().equals(expectedStatus)) {
            throw new IllegalStateException("Бронь уже имеет текущий статус");
        }
        booking.setStatus(status ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        Booking updatedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("Ошибка при загрузке Booking. Метод вернул null."));

        return bookingMapper.bookingToBookingDto(updatedBooking);
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + userId + " не найдена"));
        Booking loadedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id: " + bookingId + " не найдена"));
        User booker = loadedBooking.getBooker();
        Item item = loadedBooking.getItem();

        if (userId != booker.getId() && userId != item.getOwnerId()) {
            throw new ForbiddenUserException("Информация о брони может быть запрошена либо создателем брони, либо " +
                    "владельцем вещи, в адрес которой поступил запрос на бронь");
        }
        return bookingMapper.bookingToBookingDto(loadedBooking);
    }

    @Override
    public List<BookingDto> getBookingListCreatedByUserId(long userId, String state, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));
        QBooking booking = QBooking.booking;
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);

        BooleanExpression predicate = booking.booker.id.eq(userId);
        switch (state) {
            case "ALL":
                break;
            case "CURRENT":
                predicate = predicate.and(booking.start.loe(LocalDateTime.now())
                        .and(booking.end.goe(LocalDateTime.now())));
                break;
            case "PAST":
                predicate = predicate.and(booking.end.lt(LocalDateTime.now()));
                break;
            case "FUTURE":
                predicate = predicate.and(booking.start.gt(LocalDateTime.now()));
                break;
            case "REJECTED":
                predicate = predicate.and(booking.status.eq(Status.REJECTED));
                break;
            case "WAITING":
                predicate = predicate.and(booking.status.eq(Status.WAITING));
                break;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }

        JPAQuery<Booking> finalQuery = query.select(booking)
                .from(booking)
                .leftJoin(booking.item).fetchJoin()
                .leftJoin(booking.booker).fetchJoin()
                .where(predicate)
                .orderBy(booking.start.desc());

        if (from != null && size != null) {
            finalQuery.offset(from).limit(size);
        }

        List<Booking> bookings = finalQuery.fetch();

        return bookings.stream()
                .map(bookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingListForAllOwnerItems(long userId, String state, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));
        checkIfUserHasItems(userId);

        QBooking booking = QBooking.booking;
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        BooleanExpression predicate = booking.item.ownerId.eq(userId);
        switch (state) {
            case "ALL":
                break;
            case "CURRENT":
                predicate = predicate.and(booking.start.loe(LocalDateTime.now())
                        .and(booking.end.goe(LocalDateTime.now())));
                break;
            case "PAST":
                predicate = predicate.and(booking.end.lt(LocalDateTime.now()));
                break;
            case "FUTURE":
                predicate = predicate.and(booking.start.gt(LocalDateTime.now()));
                break;
            case "REJECTED":
                predicate = predicate.and(booking.status.eq(Status.REJECTED));
                break;
            case "WAITING":
                predicate = predicate.and(booking.status.eq(Status.WAITING));
                break;
            default:
                throw new IllegalStateException("Unknown state: " + state);
        }

        JPAQuery<Booking> finalQuery = query.select(booking)
                .from(booking)
                .leftJoin(booking.item).fetchJoin()
                .leftJoin(booking.booker).fetchJoin()
                .where(predicate)
                .orderBy(booking.start.desc());

        if (from != null && size != null) {
            finalQuery.offset(from).limit(size);
        }

        List<Booking> bookings = finalQuery.fetch();
        return bookings.stream()
                .map(bookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    private void checkIfUserHasItems(long userId) {
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);
        if (itemList.isEmpty()) {
            throw new NotFoundException("У пользователя с id: " + userId + "нет вещей во владении");
        }
    }
}
