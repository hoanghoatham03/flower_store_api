package com.example.flowerstore.controllers;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.request.UserProfileDTO;
import com.example.flowerstore.dto.request.VerifyOtpDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.dto.request.ForgotPasswordDTO;
import com.example.flowerstore.dto.request.LoginDTO;
import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.dto.response.UserPageResponse;
import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.InvalidCredentialsException;
import com.example.flowerstore.exception.InvalidTokenException;
import com.example.flowerstore.security.JwtTokenProvider;
import com.example.flowerstore.services.RefreshTokenService;
import com.example.flowerstore.services.UserService;
import com.example.flowerstore.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationMs;
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;


    //register
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        UserResponse user = userService.saveUser(registerDTO);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CREATED.value(), "User registered successfully",
                user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //login
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // check email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
    
            // create access token
            String accessToken = tokenProvider.generateToken(authentication);
    
            // create refresh token
            var user = (User) authentication.getPrincipal();
            String refreshToken = UUID.randomUUID().toString();
    
            // save refresh token to redis
            refreshTokenService.saveRefreshToken(user.getUserId().toString(), refreshToken);
    
            // send refresh token to http only cookie
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshTokenExpirationMs / 1000)
                    .build();
    
            // send access token and user to body
            Map<String, Object> data = Map.of("ACCESS_TOKEN", accessToken, "user", user);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Login successful", data);
    
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);
    
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
    

    //logout
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenValue,
            @AuthenticationPrincipal User user
    ) {
        if (refreshTokenValue == null || refreshTokenValue.isEmpty()) {
            throw new InvalidTokenException("Refresh Token is missing");
        }

        // revoke refresh token in redis
        refreshTokenService.revokeRefreshToken(user.getUserId().toString(), refreshTokenValue);

        // delete refresh token in http only cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Logged out successfully", null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }


    //refresh token
    @PostMapping("/auth/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenValue,
            @AuthenticationPrincipal User user
    ) {
        if (refreshTokenValue == null || refreshTokenValue.isEmpty()) {
            throw new InvalidTokenException("Refresh Token is missing");
    }

    // check refresh token
    boolean isValid = refreshTokenService.validateRefreshToken(user.getUserId().toString(), refreshTokenValue);
    if (!isValid) {
        throw new InvalidTokenException("Invalid or expired Refresh Token");
    }

    // create new access token
    String newAccessToken = tokenProvider.generateToken(
            new UsernamePasswordAuthenticationToken(user.getEmail(), null, new ArrayList<>())
    );

    // update ttl for refresh token
    refreshTokenService.updateTokenTTL(user.getUserId().toString(), refreshTokenValue);

    Map<String, Object> data = Map.of("ACCESS_TOKEN", newAccessToken);
    ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Access token refreshed successfully", data);
    return ResponseEntity.ok(response);
}

    //get all users for admin
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<UserPageResponse>> getUsers(@ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        UserPageResponse userPageResponse = userService.getAllUsers(pageable);

        ApiResponse<UserPageResponse> response = new ApiResponse<>(
            HttpStatus.OK.value(), 
            "Get all users successfully",
            userPageResponse
        );

        return ResponseEntity.ok(response);
    }

    //get user by userId
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable Long userId) {
        SecurityUtils.validateUserAccess(userId);
        UserResponse user = userService.getUserById(userId);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), 
            "Get user successfully",
            user);
        return ResponseEntity.ok(response);
    }

    //get user profile by userId
    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable Long userId) {
        SecurityUtils.validateUserAccess(userId);
        User user = userService.getUserProfile(userId);

        ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user profile successfully",
                user);

        return ResponseEntity.ok(response);
    }

    //update user profile by userId
    @PutMapping(value = "/users/{userId}/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<User>> updateUserProfile(@PathVariable Long userId,
        @ModelAttribute UserProfileDTO userProfileDTO) throws IOException {
        SecurityUtils.validateUserAccess(userId);
        User user = userService.updateUserProfile(userId, userProfileDTO);

        ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "Update user profile successfully",
                user);

        return ResponseEntity.ok(response);
    }

    //forgot password
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        userService.sendPasswordResetOtp(forgotPasswordDTO.getEmail());
        
        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "OTP sent successfully to your email",
            null
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/auth/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody VerifyOtpDTO verifyOtpDTO) {
        userService.verifyOtpAndResetPassword(verifyOtpDTO);
        
        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Password reset successfully",
            null
        );
        
        return ResponseEntity.ok(response);
    }

}