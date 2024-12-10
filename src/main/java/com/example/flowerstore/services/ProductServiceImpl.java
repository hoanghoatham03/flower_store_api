package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.ProductDTO;
import com.example.flowerstore.dto.response.ProductDetailResponse;
import com.example.flowerstore.dto.response.ProductResponse;
import com.example.flowerstore.entites.Category;
import com.example.flowerstore.entites.Product;
import com.example.flowerstore.entites.ProductImage;
import com.example.flowerstore.exception.NotFoundException;
import com.example.flowerstore.mapper.ProductDetailMapper;
import com.example.flowerstore.mapper.ProductMapper;
import com.example.flowerstore.repositories.CategoryRepository;
import com.example.flowerstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UploadImageFile uploadImageFile;
    private final ProductMapper productMapper;
    private final ProductDetailMapper productDetailMapper;

    // Create product for admin
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Category category = getCategoryById(productDTO.getCategoryId());
        
        Product product = Product.builder()
                .productName(productDTO.getProductName())
                .description(productDTO.getDescription())
                .stock(productDTO.getStock())
                .price(productDTO.getPrice())
                .discount(productDTO.getDiscount())
                .category(category)
                .realPrice(calculateRealPrice(productDTO.getPrice(), productDTO.getDiscount()))
                .build();
        
        Product savedProduct = productRepository.save(product);
        

        handleProductImages(savedProduct, productDTO.getImages());
        
        return productRepository.save(savedProduct);
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
    }

    private void handleProductImages(Product product, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        List<ProductImage> productImages = new ArrayList<>();
        images.forEach(imageFile -> {
            try {
                String imageUrl = uploadImageFile.uploadImage(imageFile);
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageUrl)
                        .product(product)
                        .build();
                productImages.add(productImage);
            } catch (IOException e) {
                log.error("Failed to upload image for product {}: {}", product.getProductId(), e.getMessage());
                throw new RuntimeException("Failed to upload image", e);
            }
        });
        product.getImages().addAll(productImages);
    }

    private Double calculateRealPrice(Double price, Double discount) {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        
        if (discount == null || discount == 0) {
            return price;
        }
        
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        
        return price - (price * discount / 100);
    }

    // Get all products for user
    @Override
    public List<ProductResponse> getAllProducts(Pageable pageable) {
        List<Product> products = productRepository.findAll(pageable).getContent();
        return products.stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    // Get product by id for user
    @Override
    public ProductDetailResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id = " + id));
        return productDetailMapper.toProductDetailResponse(product);
    }

    // Update product by id for admin
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        
        updateProductFields(product, productDTO);
        
        if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            deleteExistingImages(product.getImages());
            product.getImages().clear();
            handleProductImages(product, productDTO.getImages());
        }

        return productRepository.save(product);
    }

    private void updateProductFields(Product product, ProductDTO productDTO) {
        Optional.ofNullable(productDTO.getProductName()).ifPresent(product::setProductName);
        Optional.ofNullable(productDTO.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(productDTO.getStock()).ifPresent(product::setStock);
        
        if (productDTO.getPrice() != null || productDTO.getDiscount() != null) {
            Double newPrice = productDTO.getPrice() != null ? productDTO.getPrice() : product.getPrice();
            Double newDiscount = productDTO.getDiscount() != null ? productDTO.getDiscount() : product.getDiscount();
            product.setPrice(newPrice);
            product.setDiscount(newDiscount);
            product.setRealPrice(calculateRealPrice(newPrice, newDiscount));
        }

        if (productDTO.getCategoryId() != null) {
            product.setCategory(getCategoryById(productDTO.getCategoryId()));
        }
    }

    private void deleteExistingImages(List<ProductImage> existingImages) {
        if (existingImages == null) return;
        
        existingImages.forEach(image -> {
            try {
                String publicId = extractPublicId(image.getImageUrl());
                uploadImageFile.deleteImage(publicId);
            } catch (IOException e) {
                log.error("Failed to delete image {}: {}", image.getImageUrl(), e.getMessage());
                throw new RuntimeException("Failed to delete image", e);
            }
        });
        existingImages.clear();
    }

    private String extractPublicId(String imageUrl) {
        return imageUrl.substring(
            imageUrl.lastIndexOf("/") + 1,
            imageUrl.lastIndexOf(".")
        );
    }

    // Delete product by id for admin
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryCategoryId(categoryId, pageable);
        return products.getContent().stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getProductsByName(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByProductNameContaining(name, pageable);
        return products.getContent().stream()
                .map(productMapper::toProductResponse)
                .toList();
    }
} 