package com.luxury.controller;

import com.luxury.dto.ProductListResponse;
import com.luxury.dto.ProductCreateRequest;
import com.luxury.entity.Product;
import com.luxury.repository.AdProductRepository;
import com.luxury.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    // @GetMapping
    // public ResponseEntity<List<ProductListResponse>> getAllProducts() {
    //     List<Product> products = productService.getAllProducts();

    //     List<ProductListResponse> result = products.stream().map(p -> {
    //         ProductListResponse dto = new ProductListResponse();
    //         dto.setProductId(p.getProductId());
    //         dto.setName(p.getName());
    //         dto.setPrice(p.getPrice());
    //         dto.setStock(p.getStock());
    //         dto.setSoldQuantity(p.getSoldQuantity());
    //         dto.setCategoryId(p.getCategoryId());
    //         dto.setUrlImg(p.getUrlImg());
    //         return dto;
    //     }).collect(Collectors.toList());

    //     return ResponseEntity.ok(result);
    // }
    @GetMapping
    public ResponseEntity<List<ProductListResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }
    // @PostMapping
    // public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest dto) {
    //     Product product = new Product();
    //     product.setName(dto.getName());
    //     product.setDescription(dto.getDescription());
    //     product.setPrice(dto.getPrice());
    //     product.setCategoryId(dto.getCategoryId());
    //     product.setCategory_id_uu_dai(dto.getCategory_id_uu_dai());
    //     product.setCategory_id_combo(dto.getCategory_id_combo());
    //     product.setStock(dto.getStock());
    //     product.setUrlImg(dto.getUrlImg());

    //     Product saved = productService.saveProduct(product);

    //     return ResponseEntity.ok().body(
    //             java.util.Map.of("success", true, "product_id", saved.getProductId())
    //     );
    // }
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest dto) {
        Long newId = productService.createProduct(dto);
        return ResponseEntity.ok().body(
            Map.of("product_id", newId, "success", true)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductCreateRequest dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategoryId(dto.getCategoryId());
        product.setCategory_id_uu_dai(dto.getCategory_id_uu_dai());
        product.setCategory_id_combo(dto.getCategory_id_combo());
        product.setStock(dto.getStock());
        product.setUrlImg(dto.getUrlImg());

        productService.updateProduct(id, product);

        return ResponseEntity.ok().body(java.util.Map.of("success", true));
    }
//     @PutMapping("/{id}")
// public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductCreateRequest dto) {
//     try {
//         productService.updateProduct(id, dto.toProduct());
//         return ResponseEntity.ok().body(Map.of("product_id", id, "success", true));
//     } catch (Exception e) {
//         e.printStackTrace(); // Ghi rõ lỗi vào console
//         return ResponseEntity.status(500).body(Map.of("error", "Lỗi cập nhật sản phẩm: " + e.getMessage()));
//     }
// }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
    //     productService.deleteProduct(id);
    //     return ResponseEntity.ok().body(java.util.Map.of("success", true));
    // }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().body(Map.of("message", "Đã xóa sản phẩm thành công"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Không thể xóa sản phẩm vì đang được sử dụng trong giỏ hàng.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("error", "Lỗi server khi xóa sản phẩm.")
            );
        }
    }
}
