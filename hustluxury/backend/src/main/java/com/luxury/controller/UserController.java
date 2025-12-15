package com.luxury.controller;

import com.luxury.dto.ResetPasswordRequest;
import com.luxury.dto.UserProfileDTO;
import com.luxury.entity.User;
import com.luxury.exception.ErrorResponse;
import com.luxury.repository.AdUserRepository;
import com.luxury.service.UserService;
import com.luxury.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AdUserRepository userRepository;

    // Lấy thông tin profile của user hiện tại
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(HttpServletRequest request) {
        User user = jwtUtil.extractUserFromRequest(request);
        return ResponseEntity.ok(userService.getProfile(user));
    }

    // Cập nhật thông tin người dùng (nếu field nào null thì bỏ qua)
    @PostMapping("/update")
    public ResponseEntity<Void> updateProfile(HttpServletRequest request, @RequestBody UserProfileDTO dto) {
        User user = jwtUtil.extractUserFromRequest(request);
        userService.updateUser(user, dto);
        return ResponseEntity.ok().build();
    }

    // API nội bộ - tìm theo email/phone/id (giữ lại)
    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone")
    public ResponseEntity<?> getUserByPhone(@RequestParam String phone) {
        return userService.getUserByPhone(phone)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            HttpServletRequest request,
            @RequestBody ResetPasswordRequest resetRequest) {
        try {
            User user = jwtUtil.extractUserFromRequest(request);
            userService.resetPassword(
                    user,
                    resetRequest.getOldPassword(),
                    resetRequest.getNewPassword()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", e.getMessage(), request.getRequestURI()));
        }
    }
}