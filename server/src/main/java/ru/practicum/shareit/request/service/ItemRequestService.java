package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(long userId, CreatedItemRequestDto createdItemRequestDto);

    List<ItemRequestDto> getItemRequestListByUserId(long ownerId);

    List<ItemRequestDto> getAllItemRequestList(long userId, Long from, Long size);

    ItemRequestDto getRequestByItemRequestId(long userId, long requestId);


}
