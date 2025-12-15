package com.luxury.repository;

import com.luxury.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdUserRepository extends JpaRepository<User, Long> {
        Optional<User> findByPhone(String phone); 
}
