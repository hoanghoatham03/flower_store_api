package com.example.flowerstore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.flowerstore.entites.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser_UserId(Long userId);
}
