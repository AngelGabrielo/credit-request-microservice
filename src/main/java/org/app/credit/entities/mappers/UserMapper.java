package org.app.credit.entities.mappers;

import org.app.credit.entities.Role;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.entities.dtos.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "creditRequests", ignore = true)
    User toEntity(UserRegisterDto dto);

    UserResponseDto toDto(User entity);

    default List<String> mapRoles(List<Role> roles) {
        if (roles == null) return Collections.emptyList();

        return roles.stream()
                .map(Role::getName)
                .toList();
    }

}
