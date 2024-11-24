package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.entites.Order;
import java.util.List;

import org.springframework.data.domain.Pageable;
public interface OrderService {
    Order createOrderFromCart(OrderDTO orderDTO);
    List<Order> getAllOrdersForUser(Long userId);
    Order getOrderById(Long userId, Long orderId);
    List<Order> getAllOrdersForAdmin(Pageable pageable);
    Order updateOrderStatus(Long userId, Long orderId, String orderStatus);
    Order updatePaymentStatus(Long userId, Long orderId, String paymentStatus);
    void deleteOrder(Long userId, Long orderId);
}
