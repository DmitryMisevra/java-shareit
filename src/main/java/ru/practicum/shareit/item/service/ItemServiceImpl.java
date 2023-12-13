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
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;


    @Transactional
    @Override
    @NonNull
    public ItemDto createItem(@NonNull long ownerId, CreatedItemDto createdItemDto) {
        userService.getUserById(ownerId);
        Item item = Optional.ofNullable(itemMapper.createdItemDtoToItem(createdItemDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации itemDto->Item. Метод вернул null."));
        item.setOwnerId(ownerId);
        return Optional.ofNullable(itemMapper.itemToItemDto(itemRepository.save(item))).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации Item->ItemDto. Метод вернул null."));
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
        Item item = Optional.ofNullable(itemMapper.updatedItemDtoToItem(updatedItemDto))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации itemDto->Item. Метод вернул null."));
        updateditem.updateWith(item);
        return Optional.ofNullable(itemMapper.itemToItemDto(itemRepository.save(updateditem))).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации Item->ItemDto. Метод вернул null."));
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item foundItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id: " + itemId + " не найдена"));

        ItemDto foundItemDto = Optional.ofNullable(itemMapper.itemToItemDto(foundItem)).orElseThrow(() ->
                new IllegalStateException("Ошибка конвертации Item->ItemDto. Метод вернул null."));
        if (foundItemDto.getOwnerId() == userId) {
            addNextAndLastBookings(foundItemDto);
        }
        return foundItemDto;
    }

    @Override
    public List<ItemDto> getItemListByUserId(long userId) {
        return itemRepository.findItemsByOwnerIdOrderByIdAsc(userId).stream()
                .map(itemMapper::itemToItemDto)
                .map(this::addNextAndLastBookings)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(@NonNull String text) {
        return itemRepository.searchItemsByText(text).stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    private ItemDto addNextAndLastBookings(ItemDto itemDto) {
        Pageable limit = PageRequest.of(0, 1);
        Page<BookingInfoDto> lastBookingPage = bookingRepository.findLastBookingByItemId(itemDto.getId(), limit);
        Page<BookingInfoDto> nextBookingPage = bookingRepository.findNextBookingByItemId(itemDto.getId(), limit);

        Optional<BookingInfoDto> lastBookingOpt = lastBookingPage.get().findFirst();
        lastBookingOpt.ifPresent(itemDto::setLastBooking);

        Optional<BookingInfoDto> nextBookingOpt = nextBookingPage.get().findFirst();
        nextBookingOpt.ifPresent(itemDto::setNextBooking);
        return itemDto;
    }
}
