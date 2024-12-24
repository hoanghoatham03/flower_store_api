package com.example.flowerstore.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.entites.Address;
import com.example.flowerstore.entites.Cart;
import com.example.flowerstore.entites.Order;
import com.example.flowerstore.entites.OrderItem;
import com.example.flowerstore.entites.Payment;
import com.example.flowerstore.repositories.OrderRepository;
import com.example.flowerstore.repositories.CartRepository;
import com.example.flowerstore.repositories.ProductRepository;
import com.example.flowerstore.util.AppConstant;
import com.example.flowerstore.dto.response.TransactionResponse;
import com.example.flowerstore.dto.response.OrderPageResponse;
import org.springframework.data.domain.Page;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.flowerstore.dto.response.TopProductResponse;
import com.example.flowerstore.dto.response.StatisticsResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final SepayService sepayService;
    private final RedisTemplate<String, Object> redisTemplate;

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

        // Save order first to get orderId
        Order savedOrder = orderRepository.save(order);

        if (orderDTO.getPaymentId() == 2) { // Bank payment
            String transactionContent = "thanhtoanmadon" + savedOrder.getOrderId();
            String qrCode = getQrCode(savedOrder.getTotalAmount(), transactionContent);
            
            // Store payment info in Redis with 2 minutes expiration
            String paymentKey = "payment:" + savedOrder.getOrderId();
            redisTemplate.opsForValue().set(paymentKey, "PENDING", 2, TimeUnit.MINUTES);
            
            // Start payment checking schedule
            startPaymentCheckSchedule(savedOrder.getOrderId(), savedOrder.getTotalAmount(), transactionContent);
            
            savedOrder.setQrCode(qrCode);
        }

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

    private void startPaymentCheckSchedule(Long orderId, Double amount, String transactionContent) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        String paymentKey = "payment:" + orderId;
        
        executor.scheduleAtFixedRate(() -> {
            try {
                if (!redisTemplate.hasKey(paymentKey)) {
                    // Payment timeout
                    updateOrderPaymentStatus(orderId, AppConstant.PaymentStatus.FAILED);
                    executor.shutdown();
                    return;
                }
                
                if (checkPaymentStatus(orderId, amount, transactionContent)) {
                    updateOrderPaymentStatus(orderId, AppConstant.PaymentStatus.SUCCESS);
                    redisTemplate.delete(paymentKey);
                    executor.shutdown();
                }
            } catch (Exception e) {
                executor.shutdown();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public Boolean checkPaymentStatus(Long orderId, Double amount, String transactionContent) {
        try {
            List<TransactionResponse> transactions = sepayService.getTransaction(transactionContent);
            if (transactions != null) {
                return transactions.stream()
                    .anyMatch(transaction -> {
                        log.info("Checking transaction: amount={}, content={}", 
                            transaction.getAmountIn(), transaction.getTransactionContent());
                        Double transactionAmount = Double.parseDouble(transaction.getAmountIn());
                        return transactionAmount.equals(amount) && 
                               transaction.getTransactionContent().contains(transactionContent);
                    });
            }
        } catch (Exception e) {
            log.error("Error checking payment status: {}", e.getMessage());
        }
        return false;
    }

    private void updateOrderPaymentStatus(Long orderId, AppConstant.PaymentStatus status) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            order.setPaymentStatus(status);
            
            if (status == AppConstant.PaymentStatus.SUCCESS) {
                order.setOrderStatus(AppConstant.OrderStatus.PENDING);
            } else if (status == AppConstant.PaymentStatus.FAILED) {
                order.setOrderStatus(AppConstant.OrderStatus.CANCELLED);
            }
            
            orderRepository.save(order);
        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    @Override
    public String getQrCode(Double amount, String transactionContent) {
        return "https://qr.sepay.vn/img?acc=109883505777&bank=ICB&amount=" + amount + 
               "&des=SEVQR" + transactionContent + "&template=compact";
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
    public OrderPageResponse getAllOrdersForAdmin(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return new OrderPageResponse(
            orderPage.getContent(),
            orderPage.getTotalPages(),
            orderPage.getTotalElements()
        );
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

    @Override
    public StatisticsResponse getStatistics() {
        List<Order> recentOrders = orderRepository.findTop3RecentOrders();
        List<TopProductResponse> topProducts = orderRepository.findTop3MostPurchasedProducts()
            .stream()
            .limit(3)
            .toList();
        
        Double totalRevenue = orderRepository.findAll().stream()
            .filter(order -> order.getPaymentStatus() == AppConstant.PaymentStatus.SUCCESS)
            .mapToDouble(Order::getTotalAmount)
            .sum();
        
        Long totalOrders = orderRepository.count();
        Long totalProducts = productRepository.countTotalProducts();
        
        return new StatisticsResponse(
            recentOrders, 
            topProducts, 
            totalRevenue,
            totalOrders,
            totalProducts
        );
    }
}