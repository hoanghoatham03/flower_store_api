package com.example.flowerstore.controllers;

import com.example.flowerstore.dto.ApiResponse;
import com.example.flowerstore.dto.LoginDTO;
import com.example.flowerstore.dto.RegisterDTO;
import com.example.flowerstore.exception.InvalidCredentialsException;
import com.example.flowerstore.security.JwtTokenProvider;
import com.example.flowerstore.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
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

    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> register(@RequestBody RegisterDTO registerDTO) {
        RegisterDTO user = userService.saveUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @PostMapping("/login")
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
}