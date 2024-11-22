package com.example.flowerstore.controllers;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.request.UserProfileDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.dto.request.LoginDTO;
import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.InvalidCredentialsException;
import com.example.flowerstore.exception.InvalidTokenException;
import com.example.flowerstore.security.JwtTokenProvider;
import com.example.flowerstore.services.UploadImageFile;
import com.example.flowerstore.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


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
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(loginDTO.getEmail());

            
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true) //only http
                    .path("/") 
                    .maxAge(7 * 24 * 60 * 60) 
                    .build();

            
            Map<String, Object> data = Map.of("ACCESS_TOKEN", accessToken);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Login successful", data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(response);

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }



    //refresh token
    @PostMapping("/auth/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidTokenException("Refresh Token is missing");
        }

        try {
            
            if (tokenProvider.validateRefreshToken(refreshToken)) {
                String email = tokenProvider.extractEmail(refreshToken);

                
                String newAccessToken = tokenProvider.generateToken(
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>())
                );

                Map<String, Object> data = Map.of("ACCESS_TOKEN", newAccessToken);
                ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Access token refreshed successfully", data);
                return ResponseEntity.ok(response);
            } else {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }
        } catch (Exception e) {
            throw new InvalidTokenException("Error refreshing token");
        }
    }



    //get all users for admin
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<Object>> getUsers(@ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        List<UserResponse> users = userService.getAllUsers(pageable);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users successfully",
                users);

        return ResponseEntity.ok(response);
    }

    //get user by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user successfully",
                user);

        return ResponseEntity.ok(response);
    }

    //get user profile by userId
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable Long userId) {
        User user = userService.getUserProfile(userId);

        ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "Get user profile successfully",
                user);

        return ResponseEntity.ok(response);
    }

    //update user profile by userId
    @PutMapping(value = "/profile/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<User>> updateUserProfile(@PathVariable Long userId,
                                                               @ModelAttribute UserProfileDTO userProfileDTO) throws IOException {
        User user = userService.updateUserProfile(userId, userProfileDTO);

        ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "Update user profile successfully",
                user);

        return ResponseEntity.ok(response);
    }

}