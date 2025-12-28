package com.luxury.controller;

import com.luxury.dto.ProductCreateRequest;
import com.luxury.dto.ProductListResponse;
import com.luxury.entity.Product;
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
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được truy cập
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // --- 1. LẤY DANH SÁCH SẢN PHẨM ---
    // Service đã tự động map ID -> Tên (Vàng, Bạc, Bộ sưu tập...)
    @GetMapping
    public ResponseEntity<List<ProductListResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }

    // --- 2. TẠO SẢN PHẨM MỚI (KÈM UPLOAD ẢNH) ---
    // Frontend gửi: name, price, categoryId, collectionId, materialId, file (ảnh)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @ModelAttribute ProductCreateRequest dto,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // 1. Upload ảnh lên Cloudinary (nếu có)
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                dto.setUrlImg(imageUrl);
            }

            // 2. Gọi Service tạo sản phẩm
            // Lưu ý: Service sẽ tự map collectionId -> category_id_combo
            Long newId = productService.createProduct(dto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thêm sản phẩm trang sức thành công",
                    "product_id", newId
            ));

        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi ra console để debug
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi xử lý: " + e.getMessage()));
        }
    }

    // --- 3. CẬP NHẬT SẢN PHẨM ---
    // Dùng POST /update/{id} thay vì PUT để tránh lỗi trình duyệt với Multipart form
    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductCreateRequest dto,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // 1. Xử lý ảnh mới (nếu có)
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(file);
                dto.setUrlImg(imageUrl);
            }
            // Nếu file null -> dto.urlImg null -> Service sẽ giữ lại ảnh cũ trong DB

            // 2. Convert DTO -> Entity (Lúc này collectionId được map vào category_id_combo)
            Product productToUpdate = dto.toProduct();

            // 3. Gọi Service cập nhật
            productService.updateProduct(id, productToUpdate);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật sản phẩm thành công",
                    "product_id", id
            ));

        } catch (Exception e) {
            e.printStackTrace();
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
            // Lỗi này xảy ra nếu sản phẩm đã có trong đơn hàng (không thể xóa vật lý)
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Không thể xóa sản phẩm này vì đã có lịch sử giao dịch.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
}