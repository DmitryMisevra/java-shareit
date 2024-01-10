package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreatedCommentDto;
import ru.practicum.shareit.item.dto.CreatedItemDto;
import ru.practicum.shareit.item.dto.UpdatedItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long ownerId, CreatedItemDto createdItemDto) {
        return post("", ownerId, createdItemDto);
    }

    public ResponseEntity<Object> updateItem(long ownerId, long itemId, UpdatedItemDto updatedItemDto) {
        return patch("/" + itemId, ownerId, updatedItemDto);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemListByUserId(long userId, Long from, Long size) {
        Map<String, Object> parameters = new HashMap<>();
        if (from != null && size != null) {
            parameters.put("from", from);
            parameters.put("size", size);
            return get("?from={from}&size={size}", userId, parameters);
        } else {
            return get("", userId);
        }
    }

    public ResponseEntity<Object> searchItemsByText(long userId, String text, Long from, Long size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("text", text);

        if (from != null && size != null) {
            parameters.put("from", from);
            parameters.put("size", size);
            return get("/search?text={text}&from={from}&size={size}", userId, parameters);
        } else {
            return get("/search?text={text}", userId, parameters);
        }
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CreatedCommentDto createdCommentDto) {
        return post("/" + itemId + "/comment", userId, createdCommentDto);
    }
}
