package com.luxury.service;

import com.luxury.dto.ProductCreateRequest;
import com.luxury.dto.ProductListResponse;
import com.luxury.dto.ProductResponseDTO;
import com.luxury.entity.Product;
import com.luxury.exception.ProductNotFoundException;
import com.luxury.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final List<String> FIXED_CATEGORIES = List.of(
            "uu-dai", "mon-moi", "combo-1-nguoi", "combo-nhom",
            "ga-ran", "burger", "thuc-an-nhe", "do-uong"
    );

    public List<ProductResponseDTO> searchProducts(String query) {
        String q = query.trim().toLowerCase();
        List<Product> products;

        if (FIXED_CATEGORIES.contains(q)) {
            if (q.equals("combo-1-nguoi") || q.equals("combo-nhom")) {
                products = productRepository.findByCategoryCombo(q);
            } else if (q.equals("mon-moi")) {
                products = productRepository.findByCategoryMonMoi();
            } else if (q.equals("uu-dai")) {
                products = productRepository.findByCategoryUuDai(q);
            } else {
                products = productRepository.findByCategoryQuery(q);
            }

        } else {
            products = productRepository.searchProductsByKeyword(q);
        }

        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductResponseDTO getProductDTOById(Long id) {
        return productRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // --- [FIX] SỬA LẠI LOGIC UPDATE ĐỂ GIỮ ẢNH CŨ ---
    public Product updateProduct(Long id, Product newProductData) {
        // 1. Tìm sản phẩm cũ trong DB
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 2. Cập nhật thông tin (trừ ID và SoldQuantity)
        existing.setName(newProductData.getName());
        existing.setDescription(newProductData.getDescription());
        existing.setPrice(newProductData.getPrice());
        existing.setStock(newProductData.getStock());
        existing.setCategoryId(newProductData.getCategoryId());
        existing.setCategory_id_uu_dai(newProductData.getCategory_id_uu_dai());
        existing.setCategory_id_combo(newProductData.getCategory_id_combo());

        // 3. LOGIC QUAN TRỌNG: Chỉ cập nhật ảnh nếu có link mới
        // Nếu newProductData.getUrlImg() là null (do Admin không upload ảnh mới),
        // thì giữ nguyên ảnh cũ (existing.getUrlImg())
        if (newProductData.getUrlImg() != null && !newProductData.getUrlImg().isEmpty()) {
            existing.setUrlImg(newProductData.getUrlImg());
        }

        return productRepository.save(existing);
    }
    // --------------------------------------------------

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }

    private ProductResponseDTO toDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setUrlImg(product.getUrlImg());
        dto.setPrice(product.getPrice());
        return dto;
    }

    // GET - Lấy danh sách sản phẩm (cho admin)
    public List<ProductListResponse> getAllProductsForAdmin() {
        return productRepository.findAll().stream().map(product -> {
            ProductListResponse dto = new ProductListResponse();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setCategoryId(product.getCategoryId());
            dto.setCategory_id_combo(product.getCategory_id_combo());
            dto.setCategory_id_uu_dai(product.getCategory_id_uu_dai());
            dto.setStock(product.getStock());
            dto.setSoldQuantity(product.getSoldQuantity());
            dto.setUrlImg(product.getUrlImg());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getAllProductsDTO() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // POST - Tạo sản phẩm mới
    public Long createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(request.getCategoryId());
        product.setCategory_id_uu_dai(request.getCategory_id_uu_dai());
        product.setCategory_id_combo(request.getCategory_id_combo());

        // Link ảnh đã được Controller xử lý qua Cloudinary và set vào request
        product.setUrlImg(request.getUrlImg());

        return productRepository.save(product).getProductId();
    }
}