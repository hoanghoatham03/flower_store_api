package com.example.flowerstore.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flowerstore.dto.request.OrderDTO;
import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.entites.Order;
import com.example.flowerstore.services.OrderService;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

     //get all orders for admin with pagination
     @GetMapping("/admin/orders")
     public ResponseEntity<ApiResponse<List<Order>>> getAllOrdersForAdmin(@ModelAttribute PaginationDTO paginationDTO) {
         Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
         List<Order> orders = orderService.getAllOrdersForAdmin(pageable);
         return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", orders));
     }
 
     //update order status for admin
     @PutMapping("/admin/users/{userId}/orders/{orderId}/orderStatus")
     public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable Long userId, @PathVariable Long orderId, @RequestParam String orderStatus) {
         Order order = orderService.updateOrderStatus(userId, orderId, orderStatus);
         return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order status updated successfully", order));
     }
 
     //update payment status for admin
     @PutMapping("/admin/users/{userId}/orders/{orderId}/paymentStatus")
     public ResponseEntity<ApiResponse<Order>> updatePaymentStatus(@PathVariable Long userId, @PathVariable Long orderId, @RequestParam String paymentStatus) {
         Order order = orderService.updatePaymentStatus(userId, orderId, paymentStatus);
         return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Payment status updated successfully", order));
     }

    //create order from cart
    @PostMapping("users/orders")
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrderFromCart(orderDTO);
        return ResponseEntity.ok(new ApiResponse<>(
            HttpStatus.CREATED.value(),
            "Order created successfully",
            order
        ));
    }

    //get all orders for user
    @GetMapping("users/{userId}/orders")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrdersForUser(@PathVariable Long userId) {
        SecurityUtils.validateUserAccess(userId);
        List<Order> orders = orderService.getAllOrdersForUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", orders));
    }

    //get order by user id and order id
    @GetMapping("users/{userId}/orders/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long userId, @PathVariable Long orderId) {
        Order order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order fetched successfully", order));
    }

    //delete order for user
    @DeleteMapping("/orders/{orderId}/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        orderService.deleteOrder(userId, orderId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order deleted successfully", null));
    }

}
