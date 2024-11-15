package com.example.flowerstore.services;

import com.example.flowerstore.dto.request.PaginationDTO;
import com.example.flowerstore.dto.request.RegisterDTO;
import com.example.flowerstore.dto.response.UserResponse;
import com.example.flowerstore.entites.Role;
import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.AlreadyExistsException;
import com.example.flowerstore.mapper.UserMapper;
import com.example.flowerstore.mapper.UserResponseMapper;
import com.example.flowerstore.repositories.RoleRepository;
import com.example.flowerstore.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserResponseMapper userResponseMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, RoleRepository roleRepository, UserResponseMapper userResponseMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.userResponseMapper = userResponseMapper;
    }

    public UserResponse saveUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User", "email", registerDTO.getEmail());
        }

        User user = userMapper.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(new Role(2, "USER"));
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
                .map(userResponseMapper::toDTO)
                .orElseThrow();
    }
}