package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {


    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user")
            .email("user@user.com")
            .build();

    Item item = Item.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(2L)
            .available(true)
            .requestId(1L)
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(2L)
            .available(true)
            .requestId(1L)
            .build();

    Item itemSameOwner = Item.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .requestId(1L)
            .build();

    Item itemNotAvailable = Item.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(2L)
            .available(false)
            .requestId(1L)
            .build();

    CreatedBookingDto createdBookingDto = CreatedBookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .build();

    BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.WAITING)
            .build();

    BookingDto bookingDtoWithItemSameOwner = BookingDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.WAITING)
            .build();

    Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user2)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.WAITING)
            .build();

    Booking bookingWithItemSameOwner = Booking.builder()
            .id(1L)
            .item(itemSameOwner)
            .booker(user)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.WAITING)
            .build();

    Booking bookingWithStatusApproved = Booking.builder()
            .id(1L)
            .item(itemSameOwner)
            .booker(user)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.APPROVED)
            .build();

    BookingDto updatedBookingDto = BookingDto.builder()
            .id(1L)
            .item(itemDto)
            .booker(userDto)
            .start(LocalDateTime.of(2024, Month.FEBRUARY, 5, 15, 30))
            .end(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0))
            .status(Status.APPROVED)
            .build();

    @Test
    void createBooking_whenAllConditionsMet_thenCreateBooking() {
        Long creatorId = 1L;

        when(userRepository.findById(creatorId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(createdBookingDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingMapper.createdBookingDtoToBooking(createdBookingDto)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(creatorId, createdBookingDto);

        assertNotNull(result);
        assertEquals(bookingDto, result);
        verify(userRepository, times(1)).findById(creatorId);
        verify(itemRepository, times(1)).findById(createdBookingDto.getItemId());
        verify(bookingMapper, times(1)).createdBookingDtoToBooking(createdBookingDto);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).bookingToBookingDto(booking);
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowNotFoundException() {
        Long invalidUserId = -1L;

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(invalidUserId, createdBookingDto));

        verify(userRepository, times(1)).findById(invalidUserId);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowNotFoundException() {
        Long creatorId = 1L;

        when(userRepository.findById(creatorId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(createdBookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(creatorId, createdBookingDto));

        verify(userRepository, times(1)).findById(creatorId);
        verify(itemRepository, times(1)).findById(createdBookingDto.getItemId());
    }

    @Test
    void createBooking_whenBookingOwnItem_thenThrowForbiddenUserException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemSameOwner));

        assertThrows(ForbiddenUserException.class, () ->
                bookingService.createBooking(user.getId(), createdBookingDto));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowItemNotAvailableException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemNotAvailable));

        assertThrows(ItemNotAvailableException.class, () ->
                bookingService.createBooking(user.getId(), createdBookingDto));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void updateBookingStatus_whenValidRequest_thenUpdateStatus() {
        long userId = 1L;
        long bookingId = 1L;
        boolean status = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingWithItemSameOwner));
        when(bookingMapper.bookingToBookingDto(any(Booking.class))).thenReturn(bookingDtoWithItemSameOwner);

        BookingDto result = bookingService.updateBookingStatus(userId, bookingId, status);

        assertNotNull(result);
        assertEquals(bookingDtoWithItemSameOwner, result);
        verify(bookingRepository, times(1)).save(bookingWithItemSameOwner);
    }

    @Test
    void updateBookingStatus_whenUserNotFound_thenThrowNotFoundException() {
        long invalidUserId = -1L;
        long bookingId = 1L;
        boolean status = true;

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(invalidUserId, bookingId, status));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_whenBookingNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        long invalidBookingId = -1L;
        boolean status = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(invalidBookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(userId, invalidBookingId, status));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_whenNotOwner_thenThrowForbiddenUserException() {
        long userId = 1L;
        long bookingId = 1L;
        boolean status = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenUserException.class, () ->
                bookingService.updateBookingStatus(userId, bookingId, status));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_whenStatusAlreadySet_thenThrowIllegalStateException() {
        long userId = 1L;
        long bookingId = 1L;
        boolean status = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingWithStatusApproved));

        assertThrows(IllegalStateException.class, () ->
                bookingService.updateBookingStatus(userId, bookingId, status));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void findBookingById_whenValidRequest_thenReturnBooking() {
        long userId = 1L;
        long bookingId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingWithItemSameOwner));
        when(bookingMapper.bookingToBookingDto(any(Booking.class))).thenReturn(bookingDtoWithItemSameOwner);

        BookingDto result = bookingService.findBookingById(userId, bookingId);

        assertNotNull(result);
        assertEquals(bookingDto, result);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void findBookingById_whenUserNotFound_thenThrowNotFoundException() {
        long invalidUserId = -1L;
        long bookingId = 1L;

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.findBookingById(invalidUserId, bookingId));

        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void findBookingById_whenBookingNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        long invalidBookingId = -1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(invalidBookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.findBookingById(userId, invalidBookingId));
    }

    @Test
    void findBookingById_whenUserNotBookerOrOwner_thenThrowForbiddenUserException() {
        long userId = 1L;
        long bookingId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenUserException.class, () ->
                bookingService.findBookingById(userId, bookingId));
    }
}
