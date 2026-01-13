package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.gateway.client.BaseClient;

@Component
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder, serverUrl + "/users");
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("", null);
    }

    public ResponseEntity<Object> createUser(Object body) {
        return post("", null, body);
    }

    public ResponseEntity<Object> updateUser(Long userId, Object body) {
        return patch("/" + userId, null, java.util.Collections.emptyMap(), body);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId, null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId, null);
    }
}
