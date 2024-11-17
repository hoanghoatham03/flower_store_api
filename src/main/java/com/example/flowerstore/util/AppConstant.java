package com.example.flowerstore.util;

public class AppConstant {
    public static final String PAGE_NO = "0";
    public static final String PAGE_SIZE = "2";

    public enum PaymentStatus {
        PENDING,
        PAID,
        CANCELLED
    }

    public enum PaymentMethod {
        CASH,
        CREDIT_CARD,
        PAYPAL
    }

    public enum OrderStatus {
        PENDING,
        DELIVERED,
        CANCELLED
    }


}
