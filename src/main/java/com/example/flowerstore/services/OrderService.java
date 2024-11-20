package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.entites.Order;
import java.util.List;
public interface OrderService {
    Order createOrderFromCart(OrderDTO orderDTO);
    List<Order> getAllOrdersForUser(Long userId);
    Order getOrderById(Long userId, Long orderId);
    List<Order> getAllOrdersForAdmin();
    Order updateOrderStatus(Long userId, Long orderId, String orderStatus);
    Order updatePaymentStatus(Long userId, Long orderId, String paymentStatus);
    void deleteOrder(Long userId, Long orderId);
}
