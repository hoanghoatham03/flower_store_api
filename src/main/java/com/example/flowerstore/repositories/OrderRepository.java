package com.example.flowerstore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.flowerstore.entites.Order;
import com.example.flowerstore.dto.response.TopProductResponse;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_UserId(Long userId);
    Order findByUser_UserIdAndOrderId(Long userId, Long orderId);

    @Query(value = "SELECT * FROM orders ORDER BY orderDate DESC LIMIT 3", nativeQuery = true)
    List<Order> findTop3RecentOrders();

    @Query(value = "SELECT new com.example.flowerstore.dto.response.TopProductResponse(" +
           "p.productId, " +
           "p.productName, " +
           "CAST(SUM(oi.quantity) AS Integer), " +
           "p.price) " +
           "FROM OrderItem oi " +
           "JOIN oi.product p " +
           "GROUP BY p.productId, p.productName, p.price " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<TopProductResponse> findTop3MostPurchasedProducts();

    
}
