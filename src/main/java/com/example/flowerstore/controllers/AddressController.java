package com.example.flowerstore.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flowerstore.dto.request.AddressDTO;
import com.example.flowerstore.dto.response.ApiResponse;
import com.example.flowerstore.services.AddressService;
import com.example.flowerstore.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Get all addresses for admin
    @GetMapping("/admin/addresses")
    public ResponseEntity<ApiResponse<Object>> getAllAddresses() {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Addresses fetched successfully", addressService.getAllAddresses()));
    }

    // Get address by id for user
    @GetMapping("/users/{userId}/addresses/{id}")
    public ResponseEntity<ApiResponse<Object>> getAddressById(@PathVariable Long userId, @PathVariable Long id) {
        SecurityUtils.validateUserAccess(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Address fetched successfully", addressService.getAddressById(id)));
    }

    // Get all address by user id
    @GetMapping("/users/{userId}/addresses")
    public ResponseEntity<ApiResponse<Object>> getAddressByUserId(@PathVariable Long userId) {
        SecurityUtils.validateUserAccess(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Address fetched successfully", addressService.getAddressByUserId(userId)));
    }

    // Create address for user
    @PostMapping("/users/{userId}/addresses")
    public ResponseEntity<ApiResponse<Object>> createAddress(@PathVariable Long userId, @RequestBody AddressDTO addressDTO) {
        SecurityUtils.validateUserAccess(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "Address created successfully", addressService.createAddress(userId, addressDTO)));
    }

    // Update address for user
    @PutMapping("/users/{userId}/addresses/{id}")
    public ResponseEntity<ApiResponse<Object>> updateAddress(@PathVariable Long userId, @PathVariable Long id, @RequestBody AddressDTO addressDTO) {
        SecurityUtils.validateUserAccess(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Address updated successfully", addressService.updateAddress(id, addressDTO)));
    }

    // Delete address for user
    @DeleteMapping("/users/{userId}/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long userId, @PathVariable Long id) {
        SecurityUtils.validateUserAccess(userId);
        addressService.deleteAddress(id);
        return ResponseEntity.ok(new ApiResponse<Void>(HttpStatus.OK.value(), "Address deleted successfully", null));
    }
}
