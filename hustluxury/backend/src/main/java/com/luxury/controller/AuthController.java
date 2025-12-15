package com.luxury.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.luxury.dto.*;
import com.luxury.entity.User;
import com.luxury.repository.UserRepository;
import com.luxury.security.TokenBlacklistService;
import com.luxury.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail()).get();
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        log.debug("=== Admin Login Attempt ===");
        log.debug("Email from request: '{}'", request.getEmail());
        log.debug("Password length: {}", request.getPassword().length());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            log.debug("FAIL: User not found in database");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
        }

        User user = userOpt.get();
        log.debug("User found: ID={}, Email='{}', Role={}", user.getUserId(), user.getEmail(), user.getRole());

        if (!user.getRole().name().equals("ADMIN")) {
            log.debug("FAIL: User role is {} (not ADMIN)", user.getRole().name());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản không có quyền ADMIN");
        }

        log.debug("Role check passed. Checking password...");
        log.debug("Stored hash: {}", user.getHashedPassword());
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getHashedPassword());
        log.debug("Password matches: {}", passwordMatches);

        if (!passwordMatches) {
            log.debug("FAIL: Password does not match");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
        }

        log.debug("Password check passed. Attempting Spring Security authentication...");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

        try {
            authManager.authenticate(authentication);
            log.debug("Spring Security authentication passed");
        } catch (Exception e) {
            log.error("FAIL: Spring Security authentication failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
        }

        String token = jwtUtil.generateToken(user);
        log.debug("=== Admin Login SUCCESS ===");
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(409)
                    .body(Map.of(
                            "errorCode", "EMAIL_EXISTS",
                            "message", "Email already in use"
                    ));
        }

        if (userRepository.existsByPhone(req.getPhone())) {
            return ResponseEntity.status(422)
                    .body(Map.of(
                            "errorCode", "PHONE_EXISTS",
                            "message", "Phone number already in use"
                    ));
        }

        User newUser = new User();
        newUser.setFullName(req.getFullName());
        newUser.setEmail(req.getEmail());
        newUser.setPhone(req.getPhone());
        newUser.setHashedPassword(passwordEncoder.encode(req.getPassword()));
        newUser.setRole(User.Role.CUSTOMER);
        newUser.setGender(User.Gender.OTHER);

        userRepository.save(newUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registration successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing token");
        }

        String token = authHeader.substring(7);
        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
}