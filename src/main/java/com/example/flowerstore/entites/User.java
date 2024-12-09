package com.example.flowerstore.entites;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private String avatar;


    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String password;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<Address> addresses;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Cart cart;
}
