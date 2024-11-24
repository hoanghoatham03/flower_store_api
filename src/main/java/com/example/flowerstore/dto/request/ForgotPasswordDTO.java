package com.example.flowerstore.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @Email
    private String email;
} 