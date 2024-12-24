package com.example.flowerstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.example.flowerstore.entites.Order;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPageResponse {
    private List<Order> orders;
    private int totalPages;
    private long totalElements;
    private Double totalRevenue;
} 