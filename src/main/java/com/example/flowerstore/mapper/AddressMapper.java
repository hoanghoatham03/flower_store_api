package com.example.flowerstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.flowerstore.dto.request.AddressDTO;
import com.example.flowerstore.entites.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDTO toDTO(Address address);

    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "user.userId", ignore = true)
    Address toEntity(AddressDTO addressDTO);
}
