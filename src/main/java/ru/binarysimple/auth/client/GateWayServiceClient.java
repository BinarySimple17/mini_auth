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
import ru.binarysimple.auth.model.User;
import ru.binarysimple.auth.repository.UserRepository;
import ru.binarysimple.auth.security.JwtTokenProvider;

@Component
public class GateWayServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(GateWayServiceClient.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RestClient restClient;

    @Autowired
    public GateWayServiceClient(@Value("${endpoints.api-gateway:http://test-name:8081}") String baseUrl,
                                JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        logger.info("GatewayServiceClient baseUrl: {}", baseUrl);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.restClient = RestClient.builder()
//                .baseUrl("http://localhost:8200")
                .baseUrl(baseUrl)
                .build();
    }

    public CreateUserExternalDto createUser(CreateUserExternalDto user) {

        logger.debug("GatewayServiceClient createUser: {}", user);

        User userModel = userRepository.findByUsername(user.getUsername()).orElseThrow( () -> new RuntimeException("Newly created User not found"));
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