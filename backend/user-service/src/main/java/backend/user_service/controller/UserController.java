package backend.user_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.user_service.dto.LoginRequest;
import backend.user_service.dto.RegisterRequest;
import backend.user_service.model.User;
import backend.user_service.repository.UserRepository;
import backend.user_service.service.JwtService;
import backend.user_service.service.KafkaService;
import jakarta.annotation.security.PermitAll;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import backend.user_service.dto.UpdateRole;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaService kafkaService;
    
    // admin list
    private List<User> admins;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, KafkaService kafkaService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.kafkaService = kafkaService;
        this.admins = userRepository.findByRole("admin");
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isPresent() && passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            String token = jwtService.generateToken(user.get().getId(), user.get().getEmail(), user.get().getRole());
            response.put("user", user.get());

            response.put("token", token);
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200
        }

        response.put("message", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
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
        newUser.setRole(request.getUserType().toString());
        newUser.setAvatar("assets/avatars/" + request.getAvatar());
        userRepository.save(newUser);

        kafkaService.sendUserCreatedEvent(newUser);

        this.admins = userRepository.findByRole("admin");
        response.put("message", "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            response.put("user", user.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("message", "User not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/profile-update")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestParam String userId, @RequestBody User updatedUser) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findById(updatedUser.getId());

        boolean isAdmin = admins.stream().anyMatch(admin -> admin.getId().equals(userId));
        if (!userId.equals(updatedUser.getId()) && !isAdmin) {
            // confirm that the requester is the same as the user to be deleted or has admin role
            response.put("message", "Unauthorized to update this account");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAvatar(updatedUser.getAvatar());
            userRepository.save(existingUser);
            response.put("message", "User profile updated successfully");
            response.put("user", existingUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("message", "User not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/role-update")
    public ResponseEntity<Map<String, Object>> updateUserRole(@RequestParam String userId, @RequestBody UpdateRole updateRoleRequest) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findById(updateRoleRequest.getUserId());
        boolean isAdmin = admins.stream().anyMatch(admin -> admin.getId().equals(userId));
        if (!userId.equals(updateRoleRequest.getUserId()) && !isAdmin) {
            // confirm that the requester is the same as the user to be deleted or has admin role
            response.put("message", "Unauthorized to update this account");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setRole(updateRoleRequest.getRole());
            userRepository.save(existingUser);
            response.put("message", "User role updated successfully");
            response.put("user", existingUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("message", "User not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    //delete the user account
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestParam String userId, @RequestBody User userRequested) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> user = userRepository.findById(userId);
        boolean isAdmin = admins.stream().anyMatch(admin -> admin.getId().equals(userRequested.getId()));
        if (!userRequested.getId().equals(userId) && !isAdmin) {
            // confirm that the requester is the same as the user to be deleted or has admin role
            response.put("message", "Unauthorized to delete this account");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (user.isPresent()) {
            userRepository.deleteById(userId);
            kafkaService.sendUserDeletedEvent(user.get());
            response.put("message", "User deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("message", "User not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        // In a stateless JWT authentication, logout can be handled on the client side
        response.put("message", "Logout successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
