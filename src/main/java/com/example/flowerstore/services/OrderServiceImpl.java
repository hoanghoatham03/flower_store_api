package com.example.flowerstore.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.entites.Address;
import com.example.flowerstore.entites.Cart;
import com.example.flowerstore.entites.Order;
import com.example.flowerstore.entites.OrderItem;
import com.example.flowerstore.entites.Payment;
import com.example.flowerstore.mapper.OrderMapper;
import com.example.flowerstore.repositories.OrderRepository;
import com.example.flowerstore.repositories.CartRepository;
import com.example.flowerstore.util.AppConstant;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final AddressService addressService;
    private final PaymentService paymentService;

    //create order from cart
    @Override
    @Transactional
    public Order createOrderFromCart(OrderDTO orderDTO) {
        Cart cart = cartRepository.findByUser_UserId(orderDTO.getUserId());
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(AppConstant.OrderStatus.PENDING);
        order.setPaymentStatus(AppConstant.PaymentStatus.PENDING);
        order.setTotalAmount(cart.getTotalPrice());
        
        // Set address and payment
        Address address = addressService.getAddressById(orderDTO.getAddressId());
        order.setAddress(address);
        
        Payment payment = paymentService.getPaymentById(orderDTO.getPaymentId());
        order.setPayment(payment);

        // Save order first
        Order savedOrder = orderRepository.save(order);

        // Create and add order items
        cart.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOderPrice(cartItem.getPrice());
            savedOrder.addOrderItem(orderItem);
        });

        // Delete cart after creating order
        cartRepository.delete(cart);

        return orderRepository.save(savedOrder);
    }

    //get all orders for user
    @Override
    public List<Order> getAllOrdersForUser(Long userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    //get order by user id and order id
    @Override
    public Order getOrderById(Long userId, Long orderId) {
        return orderRepository.findByUser_UserIdAndOrderId(userId, orderId);
    }

    //get all orders for admin
    @Override
    public List<Order> getAllOrdersForAdmin() {
        return orderRepository.findAll();
    }

    //update order status for admin
    @Override
    public Order updateOrderStatus(Long userId, Long orderId, String orderStatus) {
        Order order = getOrderById(userId, orderId);
        order.setOrderStatus(AppConstant.OrderStatus.valueOf(orderStatus));
        return orderRepository.save(order);
    }

    //update payment status for admin
    @Override
    public Order updatePaymentStatus(Long userId, Long orderId, String paymentStatus) {
        Order order = getOrderById(userId, orderId);
        order.setPaymentStatus(AppConstant.PaymentStatus.valueOf(paymentStatus));
        return orderRepository.save(order);
    }

    //delete order for user
    @Override
    public void deleteOrder(Long userId, Long orderId) {
        //check if orderstatus is pending
        Order order = getOrderById(userId, orderId);
        if (order.getOrderStatus() == AppConstant.OrderStatus.PENDING) {
            orderRepository.delete(order);
        } else {
            throw new RuntimeException("Order status is not pending");
        }
    }

}
