package com.luxury;

import com.luxury.entity.User;
import com.luxury.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HustluxuryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HustluxuryApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@hustluxury.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setFullName("Super Admin");
                admin.setPhone("0900000000");
                admin.setHashedPassword(passwordEncoder.encode("12345"));
                admin.setRole(User.Role.ADMIN);
                admin.setGender(User.Gender.MALE);
                userRepository.save(admin);
                System.out.println(">>> Đã tạo tài khoản ADMIN thành công: " + adminEmail + " | Pass: 123456");
            }
        };
    }
}