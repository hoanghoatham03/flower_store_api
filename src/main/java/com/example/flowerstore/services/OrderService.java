package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.dto.response.OrderPageResponse;
import com.example.flowerstore.entites.Order;
import java.util.List;

import org.springframework.data.domain.Pageable;
import com.example.flowerstore.dto.response.StatisticsResponse;

public interface OrderService {
    Order createOrderFromCart(OrderDTO orderDTO);
    List<Order> getAllOrdersForUser(Long userId);
    Order getOrderById(Long userId, Long orderId);
    OrderPageResponse getAllOrdersForAdmin(Pageable pageable);
    Order updateOrderStatus(Long userId, Long orderId, String orderStatus);
    Order updatePaymentStatus(Long userId, Long orderId, String paymentStatus);
    void deleteOrder(Long userId, Long orderId);
    String getQrCode(Double amount, String transactionContent);
    Boolean checkPaymentStatus(Long orderId, Double amount, String transactionContent);
    StatisticsResponse getStatistics();
}
