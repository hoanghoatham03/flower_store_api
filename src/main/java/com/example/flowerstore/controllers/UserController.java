package com.example.flowerstore.controllers;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.dto.request.LoginDTO;
import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.exception.InvalidCredentialsException;
import com.example.flowerstore.security.JwtTokenProvider;
import com.example.flowerstore.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterDTO> register(@RequestBody RegisterDTO registerDTO) {
        RegisterDTO user = userService.saveUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),
                            loginDTO.getPassword()));

            String token = tokenProvider.generateToken(authentication);

            Map<String, Object> data = Map.of("ACCESS_TOKEN", token);

            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Login successful",
                    data);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<Object>> getUsers(@ModelAttribute PaginationDTO paginationDTO) {
        Pageable pageable = PageRequest.of(paginationDTO.getPageNo() - 1, paginationDTO.getPageSize());
        List<UserResponse> users = userService.getAllUsers(pageable);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all users successfully",
                users);

        return ResponseEntity.ok(response);
    }

}