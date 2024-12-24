package com.example.flowerstore.repositories;

import com.example.flowerstore.entites.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByProductNameContaining(String name, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT p.productId) FROM Product p")
    Long countTotalProducts();
} 