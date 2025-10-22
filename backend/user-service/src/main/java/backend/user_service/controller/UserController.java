package backend.user_service.controller;

import java.util.HashMap;
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
import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
}
