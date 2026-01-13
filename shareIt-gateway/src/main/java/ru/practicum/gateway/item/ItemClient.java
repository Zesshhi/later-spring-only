package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder, serverUrl + "/items");
    }

    public ResponseEntity<Object> createItem(Long userId, Object body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, Object body) {
        return patch("/" + itemId, userId, Map.of(), body);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, Object body) {
        return post("/" + itemId + "/comment", userId, body);
    }
}
