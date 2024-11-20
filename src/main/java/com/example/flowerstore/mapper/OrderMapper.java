package com.example.flowerstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.entites.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    
    @Mapping(target = "orderId", ignore = true)
    Order toEntity(OrderDTO orderDTO);
}
