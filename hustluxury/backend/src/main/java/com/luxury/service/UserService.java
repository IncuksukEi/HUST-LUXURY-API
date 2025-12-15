package com.luxury.service;

import com.luxury.dto.SignupRequest;
import com.luxury.dto.UserProfileDTO;
import com.luxury.entity.User;
import com.luxury.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    // Lấy thông tin profile của user
    public UserProfileDTO getProfile(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setBirthDate(user.getBirthDate());
        dto.setAddress(user.getAddress());
        return dto;
    }

    // Cập nhật thông tin người dùng
    public void updateUser(User user, UserProfileDTO dto) {
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());

        // Cập nhật gender
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            try {
                String normalizedGender = dto.getGender().toUpperCase().trim();
                user.setGender(User.Gender.valueOf(normalizedGender));
            } catch (IllegalArgumentException e) {
                // Không cập nhật nếu giá trị gender không hợp lệ
                System.out.println("Invalid gender value: " + dto.getGender());
            }
        }

        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        userRepository.save(user);
    }

    // Đăng ký user mới
    public User registerUser(SignupRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(User.Role.CUSTOMER);
        user.setGender(User.Gender.OTHER); // Mặc định gender là OTHER

        return userRepository.save(user);
    }

    // Đổi mật khẩu người dùng
    public void resetPassword(User user, String oldPassword, String newPassword) throws Exception {
        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            throw new Exception("Incorrect old password");
        }

        user.setHashedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
