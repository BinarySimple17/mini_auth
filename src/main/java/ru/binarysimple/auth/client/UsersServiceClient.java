package ru.binarysimple.auth.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import ru.binarysimple.auth.dto.CreateUserExternalDto;
import ru.binarysimple.auth.mapper.UserMapper;
import ru.binarysimple.auth.model.User;
import ru.binarysimple.auth.security.JwtTokenProvider;
import ru.binarysimple.auth.service.AuthServiceImpl;

@Component
public class UsersServiceClient {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper mapper;

    private final RestClient restClient;

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceClient.class);

    @Autowired
    public UsersServiceClient(@Value("${endpoints.api-gateway:http://test-name:8081}") String baseUrl,
                              JwtTokenProvider jwtTokenProvider, UserMapper mapper) {
        logger.info("UsersServiceClient baseUrl: {}", baseUrl);
        this.jwtTokenProvider = jwtTokenProvider;
        this.mapper = mapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public CreateUserExternalDto createUser(CreateUserExternalDto user) {

        logger.debug("UsersServiceClient createUser: {}", user);

        User userModel = mapper.toEntity(user);
        String token = jwtTokenProvider.generateToken(userModel);

        try {
        return restClient.post()
                .uri("/api/v1/user")
                .header("Authorization", "Bearer " + token)
                .body(user)
                .retrieve()
                .body(CreateUserExternalDto.class);
        } catch (HttpClientErrorException e) {
            logger.warn("Client error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e; // rethrow
        } catch (ResourceAccessException e) {
            logger.error("Network error while connecting to users service: {}", e.getMessage(), e);
            throw e; // Could be unreachable host, timeout, etc.
        } catch (Exception e) {
            logger.error("Unexpected error during user creation call", e);
            throw e;
        }
    }
}