package com.example.flowerstore.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.flowerstore.dto.request.AddressDTO;
import com.example.flowerstore.entites.Address;
import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.NotFoundException;
import com.example.flowerstore.mapper.AddressMapper;
import com.example.flowerstore.repositories.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;

    // Get all addresses
    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    // Get address by id
    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new NotFoundException("AddressId = " + id));
    }

    // Create address
    @Override
    public Address createAddress(Long userId, AddressDTO addressDTO) {
        User user = userService.getUserProfile(userId);
        
        Address address = addressMapper.toEntity(addressDTO);
        address.setUser(user);
        
        return addressRepository.save(address);
    }

    // Get address by user id
    @Override
    public List<Address> getAddressByUserId(Long userId) {
        return addressRepository.findByUser_UserId(userId);
    }

    // Update address
    @Override
    public Address updateAddress(Long id, AddressDTO addressDTO) {
        Address address = getAddressById(id);

        if (addressDTO.getStreet() != null) {
            address.setStreet(addressDTO.getStreet());
        }

        if (addressDTO.getDistrict() != null) {
            address.setDistrict(addressDTO.getDistrict());
        }

        if (addressDTO.getCity() != null) {
            address.setCity(addressDTO.getCity());
        }

        return addressRepository.save(address);
    }

    // Delete address
    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
