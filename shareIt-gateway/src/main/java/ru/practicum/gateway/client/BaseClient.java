package ru.practicum.gateway.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collections;
import java.util.Map;

public class BaseClient {

    protected static final String USER_HEADER = "X-Sharer-User-Id";

    private final RestTemplate rest;

    protected BaseClient(RestTemplateBuilder builder, String serverUrl) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, Collections.emptyMap());
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, Collections.emptyMap(), body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, Collections.emptyMap(), null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                     String path,
                                                     Long userId,
                                                     Map<String, Object> parameters,
                                                     Object body) {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set(USER_HEADER, String.valueOf(userId));
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        return rest.exchange(path, method, requestEntity, Object.class, parameters);
    }
}
