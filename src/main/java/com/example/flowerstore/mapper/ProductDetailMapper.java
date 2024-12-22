package com.example.flowerstore.mapper;

import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.entites.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "categoryName", source = "category.categoryName")
    ProductDetailResponse toProductDetailResponse(Product product);
}
