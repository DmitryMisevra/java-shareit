package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@user.com")
            .build();

    CreatedItemRequestDto createdItemRequestDto = CreatedItemRequestDto.builder()
            .description("Test description")
            .build();

    ItemRequest createdDtoItemRequest = ItemRequest.builder()
            .description("Test description")
            .build();

    ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("Test description")
            .created(LocalDateTime.now())
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("Test description")
            .created(LocalDateTime.now())
            .requestor(user)
            .build();


    @Test
    public void addItemRequest_whenUserExists_andValidDto_thenSuccess() {
        long userId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestMapper.createdItemRequestDtoToItemRequest(any(CreatedItemRequestDto.class)))
                .thenReturn(createdDtoItemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.itemRequestToItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.addItemRequest(userId, createdItemRequestDto);

        assertEquals(itemRequestDto, result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestMapper, times(1))
                .createdItemRequestDtoToItemRequest(any(CreatedItemRequestDto.class));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        verify(itemRequestMapper, times(1)).itemRequestToItemRequestDto(any(ItemRequest.class));
    }

    @Test
    public void addItemRequest_whenUserNotFound_thenThrowNotFoundException() {
        long invalidUserId = -1L;
        CreatedItemRequestDto createdDto = new CreatedItemRequestDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(invalidUserId, createdDto));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestMapper, never()).createdItemRequestDtoToItemRequest(any());
        verify(itemRequestRepository, never()).save(any());
    }
}
