package com.example.flowerstore.services;

import com.example.flowerstore.dto.RegisterDTO;
import com.example.flowerstore.entites.Role;
import com.example.flowerstore.entites.User;
import com.example.flowerstore.exception.AlreadyExistsException;
import com.example.flowerstore.exception.NotFoundException;
import com.example.flowerstore.mapper.UserMapper;
import com.example.flowerstore.repositories.RoleRepository;
import com.example.flowerstore.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    public RegisterDTO saveUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User", "email", registerDTO.getEmail());
        }

        User user = userMapper.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(new Role(2, "USER"));
        return userMapper.toDTO(userRepository.save(user));
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}