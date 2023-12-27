package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenUserException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    CreatedItemDto createdItemDto = CreatedItemDto.builder()
            .name("Дрель")
            .description("Электрическая дрель")
            .available(true)
            .build();

    Item createdItem = Item.builder()
            .name("Дрель")
            .description("Электрическая дрель")
            .available(true)
            .build();

    UpdatedItemDto updatedItemDto = UpdatedItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .available(false)
            .build();

    Item updatedItem = Item.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .ownerId(1L)
            .available(false)
            .requestId(1L)
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .build();

    ItemDto itemDtoUpd = ItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .ownerId(1L)
            .available(false)
            .build();

    BookingInfoDto lastBooking = BookingInfoDto.builder()
            .id(1L)
            .bookerId(3L)
            .build();

    BookingInfoDto nextBooking = BookingInfoDto.builder()
            .id(2L)
            .bookerId(4L)
            .build();

    CommentDto comment1 = CommentDto.builder()
            .id(1L)
            .authorName("testAuthorName1")
            .text("test comment text1")
            .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
            .build();

    CommentDto comment2 = CommentDto.builder()
            .id(2L)
            .authorName("testAuthorName2")
            .text("test comment text2")
            .created(LocalDateTime.of(2023, Month.NOVEMBER, 15, 12, 15))
            .build();

    ItemDto itemDtoWithCommentsAndBookings = ItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .ownerId(1L)
            .available(false)
            .lastBooking(lastBooking)
            .nextBooking(nextBooking)
            .comments(List.of(comment1, comment2))
            .build();

    ItemDto itemDtoWithComments = ItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .ownerId(1L)
            .available(false)
            .comments(List.of(comment1, comment2))
            .build();

    ItemDto itemDtoWithBookings = ItemDto.builder()
            .id(1L)
            .name("Дрель+")
            .description("Аккумуляторная дрель")
            .ownerId(1L)
            .available(false)
            .lastBooking(lastBooking)
            .nextBooking(nextBooking)
            .build();

    Item item = Item.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(1L)
            .available(true)
            .requestId(1L)
            .build();

    Item itemWrongOwner = Item.builder()
            .id(1L)
            .name("Дрель")
            .description("Электрическая дрель")
            .ownerId(2L)
            .available(true)
            .requestId(1L)
            .build();


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

    CreatedCommentDto createdCommentDto = CreatedCommentDto.builder()
            .text("test comment")
            .build();

    Comment createdComment = Comment.builder()
            .text("test comment")
            .build();

    Comment comment = Comment.builder()
            .id(1L)
            .text("test comment")
            .item(item)
            .author(user)
            .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
            .build();
    CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("test comment")
            .authorName("user")
            .created(LocalDateTime.of(2023, Month.DECEMBER, 10, 15, 30))
            .build();


    @Test
    public void createItem_whenValidDataProvided_thenReturnsItemDto() {
        long ownerId = 1L;

        when(userService.getUserById(ownerId)).thenReturn(userDto);
        when(itemMapper.createdItemDtoToItem(createdItemDto)).thenReturn(createdItem);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.createItem(ownerId, createdItemDto);

        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    public void updateItem_whenValidOwnerAndData_thenUpdatesItem() {
        long ownerId = 1L;
        long itemId = 1L;

        when(userService.getUserById(ownerId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.updatedItemDtoToItem(updatedItemDto)).thenReturn(updatedItem);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDtoUpd);

        ItemDto result = itemService.updateItem(ownerId, itemId, updatedItemDto);

        assertNotNull(result);
        assertEquals(itemDtoUpd, result);
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    public void updateItem_whenItemNotFound_thenThrowsNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;

        when(userService.getUserById(ownerId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(ownerId, itemId, updatedItemDto);
        });

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void updateItem_whenUserNotOwner_thenThrowsForbiddenUserException() {
        long ownerId = 1L;
        long itemId = 1L;

        when(userService.getUserById(ownerId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWrongOwner));

        assertThrows(ForbiddenUserException.class, () -> {
            itemService.updateItem(ownerId, itemId, updatedItemDto);
        });

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void getItemById_whenItemExists_andUserIsOwner_thenReturnsItemDtoWithBookingsAndComments() {
        long userId = 1L;
        long itemId = 1L;
        List<CommentDto> comments = Arrays.asList(comment1, comment2);
        Page<BookingInfoDto> lastBookingPage = new PageImpl<>(Collections.singletonList(lastBooking));
        Page<BookingInfoDto> nextBookingPage = new PageImpl<>(Collections.singletonList(nextBooking));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentsByItemId(itemId)).thenReturn(comments);
        when(bookingRepository.findLastBookingByItemId(itemId, PageRequest.of(0, 1)))
                .thenReturn(lastBookingPage);
        when(bookingRepository.findNextBookingByItemId(itemId, PageRequest.of(0, 1)))
                .thenReturn(nextBookingPage);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDtoWithCommentsAndBookings);

        ItemDto result = itemService.getItemById(userId, itemId);

        assertNotNull(result);
        assertEquals(itemDtoWithCommentsAndBookings, result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, times(1)).findCommentsByItemId(itemId);
        verify(bookingRepository, times(1))
                .findLastBookingByItemId(itemId, PageRequest.of(0, 1));
        verify(bookingRepository, times(1))
                .findNextBookingByItemId(itemId, PageRequest.of(0, 1));
    }

    @Test
    public void getItemById_whenUserIsNotOwner_thenNoBookingInfoAdded() {
        long userId = 1L;
        long itemId = 1L;
        List<CommentDto> comments = Arrays.asList(comment1, comment2);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemWrongOwner));
        when(commentRepository.findCommentsByItemId(itemId)).thenReturn(comments);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDtoWithComments);

        ItemDto result = itemService.getItemById(userId, itemId);

        assertNotNull(result);
        assertEquals(itemDtoWithComments, result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, times(1)).findCommentsByItemId(itemId);
        verify(bookingRepository, never()).findLastBookingByItemId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).findNextBookingByItemId(anyLong(), any(Pageable.class));
    }

    @Test
    public void getItemListByUserId_andUserIsOwner_withPagination_returnsPaginatedItems() {
        long userId = 1L;
        Long from = 0L;
        Long size = 10L;
        List<Item> items = Arrays.asList(item, item);
        Pageable pageable = PageRequest.of(from.intValue(), size.intValue());
        Page<BookingInfoDto> lastBookingPage = new PageImpl<>(Collections.singletonList(lastBooking));
        Page<BookingInfoDto> nextBookingPage = new PageImpl<>(Collections.singletonList(nextBooking));

        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(userId, pageable)).thenReturn(new PageImpl<>(items));
        when(bookingRepository.findLastBookingByItemId(anyLong(), any(Pageable.class))).thenReturn(lastBookingPage);
        when(bookingRepository.findNextBookingByItemId(anyLong(), any(Pageable.class))).thenReturn(nextBookingPage);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDtoWithBookings);

        List<ItemDto> result = itemService.getItemListByUserId(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findItemsByOwnerIdOrderByIdAsc(userId, pageable);
        verify(itemMapper, times(items.size())).itemToItemDto(any(Item.class));
    }

    @Test
    public void getItemListByUserId_andUserIsNotOwner_withoutPagination_returnsAllItems() {
        long userId = 1L;
        List<Item> items = Arrays.asList(item, item);
        Page<BookingInfoDto> lastBookingPage = new PageImpl<>(Collections.singletonList(lastBooking));
        Page<BookingInfoDto> nextBookingPage = new PageImpl<>(Collections.singletonList(nextBooking));

        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(userId)).thenReturn(items);
        when(bookingRepository.findLastBookingByItemId(anyLong(), any(Pageable.class))).thenReturn(lastBookingPage);
        when(bookingRepository.findNextBookingByItemId(anyLong(), any(Pageable.class))).thenReturn(nextBookingPage);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemListByUserId(userId, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findItemsByOwnerIdOrderByIdAsc(userId);
        verify(bookingRepository, times(items.size())).findLastBookingByItemId(anyLong(), any(Pageable.class));
        verify(bookingRepository, times(items.size())).findNextBookingByItemId(anyLong(), any(Pageable.class));
        verify(itemMapper, times(items.size())).itemToItemDto(any(Item.class));
    }

    @Test
    public void searchItemsByText_withPagination_returnsPaginatedItems() {
        String searchText = "test";
        Long from = 0L;
        Long size = 10L;
        List<Item> items = Arrays.asList(item, item);
        Pageable pageable = PageRequest.of(from.intValue(), size.intValue());

        when(itemRepository.searchItemsByText(searchText, pageable)).thenReturn(new PageImpl<>(items));
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItemsByText(searchText, from, size);

        assertNotNull(result);
        assertEquals(items.size(), result.size());
        verify(itemRepository, times(1)).searchItemsByText(searchText, pageable);
        verify(itemMapper, times(items.size())).itemToItemDto(any(Item.class));
    }

    @Test
    public void searchItemsByText_withoutPagination_returnsAllMatchingItems() {
        String searchText = "test";
        List<Item> items = Arrays.asList(item, item);

        when(itemRepository.searchItemsByText(searchText)).thenReturn(items);
        when(itemMapper.itemToItemDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItemsByText(searchText, null, null);

        assertNotNull(result);
        assertEquals(items.size(), result.size());
        verify(itemRepository, times(1)).searchItemsByText(searchText);
        verify(itemMapper, times(items.size())).itemToItemDto(any(Item.class));
    }

    @Test
    public void addComment_whenValidRequest_thenReturnsCommentDto() {
        Long userId = 1L;
        Long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.checkIfCompletedBookingExistsForItemByUserId(userId, itemId)).thenReturn(true);
        when(commentMapper.createdCoomentDtoToComment(createdCommentDto)).thenReturn(createdComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentMapper.commentToCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.addComment(userId, itemId, createdCommentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).checkIfCompletedBookingExistsForItemByUserId(userId, itemId);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).commentToCommentDto(any(Comment.class));
    }

    @Test
    public void addComment_whenUserNotFound_thenThrowsNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(userId, itemId, createdCommentDto);
        });

        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    public void addComment_whenItemNotFound_thenThrowsNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(userId, itemId, createdCommentDto);
        });
    }

    @Test
    public void addComment_whenNoCompletedBookings_thenThrowsIllegalStateException() {
        Long userId = 1L;
        Long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.checkIfCompletedBookingExistsForItemByUserId(userId, itemId)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> {
            itemService.addComment(userId, itemId, createdCommentDto);
        });
    }
}
