package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Transactional
    @Override
    @NonNull
    public ItemDto createItem(@NonNull long ownerId, CreatedItemDto createdItemDto) {
        userService.getUserById(ownerId);
        Item item = itemMapper.createdItemDtoToItem(createdItemDto);
        item.setOwnerId(ownerId);
        return itemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(@NonNull long ownerId, long itemId, UpdatedItemDto updatedItemDto) {
        userService.getUserById(ownerId);
        Item updateditem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + itemId + " не найдена"));
        if (updateditem.getOwnerId() != ownerId) {
            throw new ForbiddenUserException("Данные о вещи может обновлять только владелец");
        }
        Item item = itemMapper.updatedItemDtoToItem(updatedItemDto);
        updateditem.updateWith(item);
        return itemMapper.itemToItemDto(itemRepository.save(updateditem));
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item foundItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + itemId + " не найдена"));
        if (foundItem.getOwnerId() == userId) {
            addNextAndLastBookings(foundItem);
        }
        foundItem.setComments(commentRepository.findCommentsByItemId(itemId));
        return itemMapper.itemToItemDto(foundItem);
    }

    @Override
    public List<ItemDto> getItemListByUserId(long userId, Long from, Long size) {
        if (from != null && size != null) {
            Pageable pageable = PageRequest.of(from.intValue(), size.intValue());
            return itemRepository.findItemsByOwnerIdOrderByIdAsc(userId, pageable).stream()
                    .map(this::addNextAndLastBookings)
                    .map(itemMapper::itemToItemDto)
                    .collect(Collectors.toList());
        } else {
            return itemRepository.findItemsByOwnerIdOrderByIdAsc(userId).stream()
                    .map(this::addNextAndLastBookings)
                    .map(itemMapper::itemToItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<ItemDto> searchItemsByText(@NonNull String text, Long from, Long size) {
        if (from != null && size != null) {
            Pageable pageable = PageRequest.of(from.intValue(), size.intValue());
            return itemRepository.searchItemsByText(text, pageable).stream()
                    .map(itemMapper::itemToItemDto)
                    .collect(Collectors.toList());
        } else {
            return itemRepository.searchItemsByText(text).stream()
                    .map(itemMapper::itemToItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CreatedCommentDto createdCommentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + itemId + " не найдена"));
        if (!bookingRepository.checkIfCompletedBookingExistsForItemByUserId(userId, itemId)) {
            throw new IllegalStateException("У пользователя с id: " + userId + " нет завершенных букингов с id "
                    + itemId);
        }

        Comment comment = commentMapper.createdCoomentDtoToComment(createdCommentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        Comment savedComment = commentRepository.save(comment);
        Comment uploadedComment = commentRepository.findById(savedComment.getId())
                .orElseThrow(() -> new IllegalStateException("Ошибка при загрузке Comment." +
                        " Метод вернул null."));
        return commentMapper.commentToCommentDto(uploadedComment);
    }

    private Item addNextAndLastBookings(Item item) {
        Pageable limit = PageRequest.of(0, 1);
        Page<BookingInfoDto> lastBookingPage = bookingRepository.findLastBookingByItemId(item.getId(), limit);
        Page<BookingInfoDto> nextBookingPage = bookingRepository.findNextBookingByItemId(item.getId(), limit);

        Optional<BookingInfoDto> lastBookingOpt = lastBookingPage.get().findFirst();
        lastBookingOpt.ifPresent(item::setLastBooking);

        Optional<BookingInfoDto> nextBookingOpt = nextBookingPage.get().findFirst();
        nextBookingOpt.ifPresent(item::setNextBooking);
        return item;
    }
}
