package com.ecommerce.user_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.RegisterRequest;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        //TODO: Find the user in the database by their email

        //TODO: Verify the user's password


        response.put("message", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PermitAll
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        //TODO: Create a new user in the database

        response.put("message", "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
