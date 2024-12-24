package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.request.UserProfileDTO;
import com.example.flowerstore.dto.request.VerifyOtpDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.dto.response.UserPageResponse;
import com.example.flowerstore.entites.User;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
public interface UserService {
    UserResponse saveUser(RegisterDTO registerDTO);
    UserPageResponse getAllUsers(Pageable pageable);
    UserResponse getUserById(Long userId);
    User getUserProfile(Long userId);
    User updateUserProfile(Long userId, UserProfileDTO userProfileDTO);
    void sendPasswordResetOtp(String email);
    void verifyOtpAndResetPassword(VerifyOtpDTO verifyOtpDTO);
    Optional<User> getUserByEmail(String email);
}