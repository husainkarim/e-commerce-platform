package com.ecommerce.user_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.model.User;
import com.ecommerce.user_service.repository.UserRepository;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final JwtService jwtService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        //TODO: Find the user in the database by their email
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        

        //TODO: Verify the user's password


        response.put("message", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PermitAll
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response.put("error", "Email is already in use!");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setRole(request.getUserType());
        newUser.setAvatar(request.getAvatar());
        // userRepository.save(newUser);

        response.put("message", "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
