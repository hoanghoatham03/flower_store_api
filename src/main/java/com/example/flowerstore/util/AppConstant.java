package com.example.flowerstore.util;

public class AppConstant {

    //define admin list
    public static final String[] ADMIN_LIST = {
        "/api/admin/**"
    };

    //define user list
    public static final String[] USER_LIST = {
        "/api/users/**"
    };

    // define public list
    public static final String[] PUBLIC_LIST = {
        "/api/categories/**",
        "/api/products/**",
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    };

    

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED
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


