package com.luxury.controller;

import com.luxury.dto.ProductCreateRequest;
import com.luxury.dto.ProductListResponse;
import com.luxury.entity.Product;
import com.luxury.repository.AdProductRepository;
import com.luxury.service.CloudinaryService;
import com.luxury.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AdProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // --- 1. LẤY DANH SÁCH SẢN PHẨM ---
    @GetMapping
    public ResponseEntity<List<ProductListResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }

    // --- 2. TẠO SẢN PHẨM MỚI (KÈM UPLOAD ẢNH) ---
    // Method: POST
    // Sử dụng @ModelAttribute để nhận cả file và dữ liệu text cùng lúc
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @ModelAttribute ProductCreateRequest dto,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // Nếu có file ảnh được gửi lên -> Upload và lấy link
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                dto.setUrlImg(imageUrl);
            }

            // Lưu sản phẩm vào DB
            Long newId = productService.createProduct(dto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thêm sản phẩm thành công",
                    "product_id", newId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi xử lý: " + e.getMessage()));
        }
    }

    // --- 3. CẬP NHẬT SẢN PHẨM (KÈM UPLOAD ẢNH) ---
    // Method: POST (Lưu ý: Dùng POST với đường dẫn /update/{id} thay vì PUT để tránh lỗi file)
    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductCreateRequest dto,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // Bước 1: Xử lý ảnh (Nếu có ảnh mới thì upload, không thì thôi)
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                dto.setUrlImg(imageUrl);
            }
            // Nếu file null -> dto.urlImg sẽ null -> ProductService sẽ giữ nguyên ảnh cũ.

            // Bước 2: Gọi Service cập nhật
            Product productToUpdate = dto.toProduct();
            productService.updateProduct(id, productToUpdate);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật sản phẩm thành công",
                    "product_id", id
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi cập nhật: " + e.getMessage()));
        }
    }

    // --- 4. XÓA SẢN PHẨM ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm thành công"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Không thể xóa sản phẩm này vì đang nằm trong đơn hàng lịch sử.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
}