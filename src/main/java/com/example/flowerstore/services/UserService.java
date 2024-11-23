package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.request.UserProfileDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.entites.Role;
import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.AlreadyExistsException;
import com.example.flowerstore.exception.NotFoundException;
import com.example.flowerstore.mapper.UserMapper;
import com.example.flowerstore.mapper.UserResponseMapper;
import com.example.flowerstore.repositories.RoleRepository;
import com.example.flowerstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserResponseMapper userResponseMapper;
    private final UploadImageFile uploadImageFile;


    public UserResponse saveUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User", "email", registerDTO.getEmail());
        }

        User user = userMapper.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Get the USER role from database
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
        user.setRole(userRole);
        
        return userResponseMapper.toDTO(userRepository.save(user));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.getContent().stream()
                .map(userResponseMapper::toDTO)
                .toList();
    }

    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userResponseMapper::toDTO).orElseThrow(() -> new NotFoundException("userId = "+ userId));
    }

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("userId = "+ userId));
    }

    public User updateUserProfile(Long userId, UserProfileDTO userProfileDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("userId = "+ userId));

        if (userProfileDTO.getFirstName() != null && !userProfileDTO.getFirstName().isEmpty()) {
            user.setFirstName(userProfileDTO.getFirstName());
        }

        if (userProfileDTO.getLastName() != null && !userProfileDTO.getLastName().isEmpty()) {
            user.setLastName(userProfileDTO.getLastName());
        }

        if (userProfileDTO.getMobileNumber() != null && !userProfileDTO.getMobileNumber().isEmpty()) {
            user.setMobileNumber(userProfileDTO.getMobileNumber());
        }

        if (userProfileDTO.getAvatar() != null && !userProfileDTO.getAvatar().isEmpty()) {

            try {
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    String publicId = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1, user.getAvatar().lastIndexOf("."));
                    String avatarUrl = uploadImageFile.uploadOverwriteImage(userProfileDTO.getAvatar(), publicId);
                    user.setAvatar(avatarUrl);
                } else {
                    String avatarUrl = uploadImageFile.uploadImage(userProfileDTO.getAvatar());
                    user.setAvatar(avatarUrl);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return userRepository.save(user);

    }
}