package com.example.flowerstore.security;

import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    
    public static void validateUserAccess(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        
        if (!currentUser.getRole().getName().equals("ADMIN") && !currentUser.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to access this resource");
        }
    }
}