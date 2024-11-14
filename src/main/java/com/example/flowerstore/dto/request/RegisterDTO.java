package com.example.flowerstore.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    private String firstName;

    private String lastName;

    private String mobileNumber;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;
}
