package ru.binarysimple.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.binarysimple.auth.dto.*;
import ru.binarysimple.auth.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toUserDto(User user);

    User toEntity(UserDtoAll userDtoAll);

    User toEntity(CreateUserExternalDto userDtoExternal);

    UserDtoAll toUserDtoAll(User user);

    User updateWithNull(UserDtoAll userDtoAll, @MappingTarget User user);

    User toEntity(LoginRequest userDto);

    LoginRequest toLoginRequestDto(User user);

    User toEntity(RegisterRequest userDto);

    RegisterRequest toRegisterRequestDto(User user);

    CreateUserExternalDto toCreateUserExternalDto(RegisterRequest user);

    UserInfo toUserInfo(User user);
}