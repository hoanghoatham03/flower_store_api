package com.example.flowerstore.mapper;

import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.entites.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserResponseMapper {
    UserResponse toDTO(User user);
    User toEntity(UserResponse userResponse);
}
