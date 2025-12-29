package ru.binarysimple.auth.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.binarysimple.auth.dto.CreateUserExternalDto;
import ru.binarysimple.auth.model.User;
import ru.binarysimple.auth.service.AuthServiceImpl;

@Component
public class UsersServiceClient {

    private final RestClient restClient;

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceClient.class);

    public UsersServiceClient(@Value("${endpoints.api-gateway:http://test-name:8081}") String baseUrl) {
//    public UsersServiceClient() {
        logger.info("UsersServiceClient baseUrl: {}", baseUrl);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public CreateUserExternalDto createUser(CreateUserExternalDto user) {
        
        logger.debug("UsersServiceClient createUser: {}", user);

        return restClient.post()
                .uri("/user")
                .body(user)
                .retrieve()
                .body(CreateUserExternalDto.class);
    }
}