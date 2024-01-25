package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.CreatedItemRequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(long userId, CreatedItemRequestDto createdItemRequestDto) {
        return post("", userId, createdItemRequestDto);
    }

    public ResponseEntity<Object> getItemRequestListByUserId(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getAllItemRequestList(long userId, Long from, Long size) {
        Map<String, Object> parameters = new HashMap<>();
        if (from != null && size != null) {
            parameters.put("from", from);
            parameters.put("size", size);
            return get("/all?from={from}&size={size}", userId, parameters);
        } else {
            return get("/all", userId);
        }
    }

    public ResponseEntity<Object> getRequestByItemRequestId(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}
