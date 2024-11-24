package com.example.flowerstore.config;

import com.example.flowerstore.repositories.UserRepository;
import com.example.flowerstore.security.AuthFilter;
import com.example.flowerstore.services.UserDtlsImpl;
import com.example.flowerstore.util.AppConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    @Lazy
    private AuthFilter authFilter;
    @Autowired
    private UserRepository userRepository;
    public SecurityConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

   

    // filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(auths -> auths
                        .requestMatchers(AppConstant.ADMIN_LIST).hasRole("ADMIN")
                        .requestMatchers(AppConstant.USER_LIST).hasRole("USER")
                        .requestMatchers(AppConstant.PUBLIC_LIST).permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDtlsImpl userDetailsService() {
        return new UserDtlsImpl(userRepository);
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}