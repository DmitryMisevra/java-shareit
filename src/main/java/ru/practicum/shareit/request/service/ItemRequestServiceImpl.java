package ru.practicum.shareit.request.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(long userId, CreatedItemRequestDto createdItemRequestDto) {
        User requestor = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestMapper.createdItemRequestDtoToItemRequest(createdItemRequestDto);
        itemRequest.setRequestor(requestor);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.itemRequestToItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestListByUserId(long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + ownerId + " не найден"));

        QItemRequest qItemRequest = QItemRequest.itemRequest;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<ItemRequestDto> itemRequestDtos = queryFactory
                .select(Projections.constructor(ItemRequestDto.class,
                        qItemRequest.id,
                        qItemRequest.description,
                        qItemRequest.created))
                .from(qItemRequest)
                .where(qItemRequest.requestor.id.eq(ownerId))
                .orderBy(qItemRequest.created.desc())
                .fetch();

        itemRequestDtos.forEach(dto -> {
            List<ItemDto> items = findItemsByRequestId(dto.getId());
            dto.setItems(items);
        });
        return itemRequestDtos;
    }


    @Override
    public List<ItemRequestDto> getAllItemRequestList(long userId, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));

        QItemRequest qItemRequest = QItemRequest.itemRequest;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        JPAQuery<ItemRequestDto> query = queryFactory
                .select(Projections.constructor(ItemRequestDto.class,
                        qItemRequest.id,
                        qItemRequest.description,
                        qItemRequest.created))
                .from(qItemRequest)
                .where(qItemRequest.requestor.id.ne(userId))
                .orderBy(qItemRequest.created.desc());

        if (from != null && size != null) {
            query.offset(from).limit(size);
        }

        List<ItemRequestDto> itemRequestDtos = query.fetch();

        itemRequestDtos.forEach(dto -> {
            List<ItemDto> items = findItemsByRequestId(dto.getId());
            dto.setItems(items);
        });
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequestByItemRequestId(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с id: " + requestId + " не найден"));
        itemRequest.setItems(findItemsByRequestId(requestId));
        return (itemRequestMapper.itemRequestToItemRequestDto(itemRequest));
    }

    private List<ItemDto> findItemsByRequestId(Long requestId) {
        QItem qItem = QItem.item;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        return queryFactory
                .select(Projections.constructor(ItemDto.class,
                        qItem.id,
                        qItem.name,
                        qItem.description,
                        qItem.ownerId,
                        qItem.available,
                        qItem.requestId))
                .from(qItem)
                .where(qItem.requestId.eq(requestId))
                .fetch();
    }
}


