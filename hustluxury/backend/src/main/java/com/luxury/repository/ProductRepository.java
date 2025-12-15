package com.luxury.repository;

import com.luxury.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p JOIN p.category c WHERE LOWER(c.query) = LOWER(:query)")
    List<Product> findByCategoryQuery(String query);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:keyword% OR LOWER(p.description) LIKE %:keyword%")
    List<Product> searchProductsByKeyword(String keyword);

    @Query("SELECT p FROM Product p ORDER BY p.soldQuantity DESC")
    List<Product> findTopSellingProducts();

    @Query(value="SELECT p.* FROM Products p JOIN Categories c ON LOWER(p.category_id_combo) = LOWER(c.category_id) WHERE c.query = ?1", nativeQuery = true)
    List<Product> findByCategoryCombo(String query);

    @Query(value="SELECT p.* FROM Products p JOIN Categories c ON LOWER(p.category_id_uu_dai) = LOWER(c.category_id) WHERE c.query = ?1", nativeQuery = true)
    List<Product> findByCategoryUuDai(String query);

    @Query(value="SELECT * FROM Products LIMIT 12", nativeQuery = true)
    List<Product> findByCategoryMonMoi();
}