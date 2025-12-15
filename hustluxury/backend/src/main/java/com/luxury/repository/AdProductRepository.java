package com.luxury.repository;

import com.luxury.entity.Product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name); // <- Dòng mới thêm
}
