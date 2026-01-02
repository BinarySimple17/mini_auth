package ru.binarysimple.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.binarysimple.auth.dto.*;
import ru.binarysimple.auth.filter.UserFilter;
import ru.binarysimple.auth.model.User;

import java.io.IOException;
import java.util.List;

public interface AuthService {
//    Page<UserDtoAll> getAll(UserFilter filter, Pageable pageable);
//
//    UserDtoAll getOne(Long id);
//
//    List<UserDtoAll> getMany(List<Long> ids);
//
//    UserDtoAll create(UserDtoAll dto);
//
//    UserDtoAll patch(Long id, JsonNode patchNode) throws IOException;
//
//    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;
//
//    UserDtoAll delete(Long id);
//
//    void deleteMany(List<Long> ids);

    AuthResponse register(RegisterRequest dto);

    AuthResponse login(LoginRequest loginRequest);

    Boolean validateToken(String token);

    public UserInfo getUserInfo(String token);

    void logout(String token);

}
