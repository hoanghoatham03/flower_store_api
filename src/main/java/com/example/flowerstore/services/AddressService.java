package com.example.flowerstore.services;

import java.util.List;

import com.example.flowerstore.dto.request.AddressDTO;
import com.example.flowerstore.entites.Address;

public interface AddressService {
    List<Address> getAllAddresses();
    Address getAddressById(Long id);
    Address createAddress(Long userId, AddressDTO addressDTO);
    List<Address> getAddressByUserId(Long userId);
    Address updateAddress(Long id, AddressDTO addressDTO);
    void deleteAddress(Long id);
}
