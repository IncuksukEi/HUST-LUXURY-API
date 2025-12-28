package com.luxury.repository;

import com.luxury.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Tìm theo Danh mục chính (Necklaces, Rings...) 
    List<Product> findByCategoryId(Long categoryId);

    // 2. Tìm theo Bộ Sưu Tập (Tái sử dụng cột category_id_combo)
    // Sửa lại để tìm chính xác theo ID thay vì join bảng
    @Query("SELECT p FROM Product p WHERE p.category_id_combo = :collectionId")
    List<Product> findByCollectionId(Long collectionId);

    // Giữ lại hàm cũ nếu muốn tìm theo String query (native) nhưng khuyên dùng hàm trên
    @Query(value="SELECT p.* FROM Products p WHERE p.category_id_combo = ?1", nativeQuery = true)
    List<Product> findByCategoryCombo(String query);

    // 3. Tìm theo Chất Liệu (Tái sử dụng cột category_id_uu_dai)
    @Query("SELECT p FROM Product p WHERE p.category_id_uu_dai = :materialId")
    List<Product> findByMaterialId(Long materialId);

    @Query(value="SELECT p.* FROM Products p WHERE p.category_id_uu_dai = ?1", nativeQuery = true)
    List<Product> findByCategoryUuDai(String query);

    // 4. Tìm kiếm từ khóa chung
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE %:keyword% OR LOWER(p.description) LIKE %:keyword%")
    List<Product> searchProductsByKeyword(String keyword);

    // 5. Logic cũ (Món mới -> Sản phẩm mới)
    @Query(value="SELECT * FROM Products ORDER BY created_at DESC LIMIT 12", nativeQuery = true)
    List<Product> findNewArrivals();

    @Query("SELECT p FROM Product p ORDER BY p.soldQuantity DESC")
    List<Product> findTopSellingProducts();

    // Hỗ trợ query cũ
    @Query("SELECT p FROM Product p JOIN p.category c WHERE LOWER(c.query) = LOWER(:query)")
    List<Product> findByCategoryQuery(String query);

    @Query(value="SELECT * FROM Products LIMIT 12", nativeQuery = true)
    List<Product> findByCategoryMonMoi();
}