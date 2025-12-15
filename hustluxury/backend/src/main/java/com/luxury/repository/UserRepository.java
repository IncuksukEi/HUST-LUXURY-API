package com.luxury.repository;

import com.luxury.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.userId = :id")
    Optional<User> findById(Long id);

    // Kiểm tra tồn tại email
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

    // Kiểm tra tồn tại số điện thoại
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phone = :phone")
    boolean existsByPhone(String phone);
}
