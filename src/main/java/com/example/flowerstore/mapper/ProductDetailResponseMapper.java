package com.example.flowerstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.entites.Product;

@Mapper(componentModel = "spring")
public interface ProductDetailResponseMapper {
    @Mapping(target = "categoryName", source = "category.categoryName")
    ProductDetailResponse toProductDetailResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Product toEntity(ProductDetailResponse productDetailResponse);
}
