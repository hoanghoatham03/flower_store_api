package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.ProductDTO;
import com.example.flowerstore.dto.response.ProductResponse;
import com.example.flowerstore.entites.Category;
import com.example.flowerstore.entites.Product;
import com.example.flowerstore.entites.ProductImage;
import com.example.flowerstore.exception.NotFoundException;
import com.example.flowerstore.mapper.ProductMapper;
import com.example.flowerstore.repositories.CategoryRepository;
import com.example.flowerstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UploadImageFile uploadImageFile;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("categoryId = " + productDTO.getCategoryId()));

        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setStock(productDTO.getStock());
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        product.setRealPrice(calculateRealPrice(productDTO.getPrice(), productDTO.getDiscount()));
        product.setCategory(category);

        // Save product first to get the ID
        Product finalProduct = productRepository.save(product);

        // Handle image uploads
        List<ProductImage> productImages = new ArrayList<>();
        if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            productDTO.getImages().forEach(imageFile -> {
                try {
                    String imageUrl = uploadImageFile.uploadImage(imageFile);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImages.add(productImage);
                    productImage.setProduct(finalProduct);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            });
        }
        finalProduct.setImages(productImages);

        return productRepository.save(finalProduct);
    }

    private Double calculateRealPrice(Double price, Double discount) {
        if (discount == null || discount == 0) {
            return price;
        }
        return price - (price * discount / 100);
    }

    @Override
    public List<ProductResponse> getAllProducts(Pageable pageable) {
        List<Product> products = productRepository.findAll(pageable).getContent();
        return products.stream()
                .map(productMapper::toProductResponse)
                .toList();
    }
} 