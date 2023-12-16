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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    @PersistenceContext
    private EntityManager entityManager;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public BookingDto createBooking(long creatorId, CreatedBookingDto createdBookingDto) {
        User booker = findUserById(creatorId);
        Item item = findItemById(creatorId, createdBookingDto.getItemId());

        if (item.getOwnerId() == creatorId) {
            throw new ForbiddenUserException("Вы не можете забронировать вашу вещь");
        }

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь с id: " + item.getId() + " недоступна для брони в данный момент");
        }

        Booking booking = Optional.ofNullable(bookingMapper.createdBookingDtoToBooking(createdBookingDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации BookingDto->Booking. Метод вернул " +
                        "null."));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        Booking loadedBooking = bookingRepository.findById(savedBooking.getId())
                .orElseThrow(() -> new IllegalStateException("Ошибка при загрузке Booking. Метод вернул null."));

        return Optional.ofNullable(bookingMapper.bookingToBookingDto(loadedBooking))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации Booking ->BookingDto. Метод вернул " +
                        "null."));
    }

    @Transactional
    @Override
    public BookingDto updateBookingStatus(long userId, long bookingId, boolean status) {
        findUserById(userId);
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

        return Optional.ofNullable(bookingMapper.bookingToBookingDto(updatedBooking))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации Booking ->BookingDto. Метод вернул " +
                        "null."));
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        findUserById(userId);
        Booking loadedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id: " + bookingId + " не найдена"));
        User booker = loadedBooking.getBooker();
        Item item = loadedBooking.getItem();

        if (userId != booker.getId() && userId != item.getOwnerId()) {
            throw new ForbiddenUserException("Информация о брони может быть запрошена либо создателем брони, либо " +
                    "владельцем вещи, в адрес которой поступил запрос на бронь");
        }
        return Optional.ofNullable(bookingMapper.bookingToBookingDto(loadedBooking))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации Booking ->BookingDto. Метод вернул " +
                        "null."));
    }

    @Override
    public List<BookingDto> getBookingListCreatedByUserId(long userId, String state) {
        findUserById(userId);

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

        List<Booking> bookings = query.select(booking)
                .from(booking)
                .leftJoin(booking.item).fetchJoin()
                .leftJoin(booking.booker).fetchJoin()
                .where(predicate)
                .orderBy(booking.start.desc())
                .fetch();

        return bookings.stream()
                .map(bookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingListForAllOwnerItems(long userId, String state) {
        findUserById(userId);
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
        List<Booking> bookings = query.select(booking)
                .from(booking)
                .leftJoin(booking.item).fetchJoin()
                .leftJoin(booking.booker).fetchJoin()
                .where(predicate)
                .orderBy(booking.start.desc())
                .fetch();
        return bookings.stream()
                .map(bookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    private User findUserById(long id) {
        UserDto userDto = userService.getUserById(id);
        return Optional.ofNullable(userMapper.userDtoToUser(userDto)).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
    }

    private Item findItemById(long userId, long itemId) {
        ItemDto itemDto = itemService.getItemById(userId, itemId);
        return Optional.ofNullable(itemMapper.itemDtoToItem(itemDto)).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации ItemDto->Item. Метод вернул null."));
    }

    private void checkIfUserHasItems(long userId) {
        List<ItemDto> itemList = itemService.getItemListByUserId(userId);
        if (itemList.isEmpty()) {
            throw new NotFoundException("У пользователя с id: " + userId + "нет вещей во владении");
        }
    }
}
