package com.example.flowerstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.example.flowerstore.entites.Order;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
    private List<Order> recentOrders;
    private List<TopProductResponse> topProducts;
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalProducts;
} 