package com.example.flowerstore.mapper;

import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.entites.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    RegisterDTO toDTO(User user);

    User toEntity(RegisterDTO registerDTO);

}
