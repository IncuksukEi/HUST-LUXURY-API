package com.luxury.controller;

import com.luxury.dto.ProductResponseDTO;
import com.luxury.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        // Bạn có thể dùng lại hàm search với query rỗng để lấy tất cả
        // Hoặc gọi hàm getAllProducts() nếu bên Service đã có
        return productService.getAllProductsDTO();
    }
    // Tìm kiếm sản phẩm theo từ khoá hoặc category
    @GetMapping("/search")
    public List<ProductResponseDTO> searchProducts(@RequestParam("q") String query) {
        return productService.searchProducts(query);
    }

    // Lấy thông tin sản phẩm chi tiết theo ID
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getProductDTOById(id);
    }

}