package com.example.flowerstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpDTO {
    @Email
    private String email;
    @NotBlank
    private String otp;
    @NotBlank
    private String newPassword;
} 