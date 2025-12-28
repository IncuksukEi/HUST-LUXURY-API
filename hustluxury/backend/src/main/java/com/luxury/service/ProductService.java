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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // --- CẤU HÌNH MAPPING DỮ LIỆU CỨNG (Vì DB lưu ID) ---

    // 1. CHẤT LIỆU (Material) -> Map vào category_id_uu_dai
    private static final Map<String, Long> MATERIAL_SLUG_MAP = new HashMap<>(); // map "gold" -> 1
    private static final Map<Long, String> MATERIAL_NAME_MAP = new HashMap<>(); // map 1 -> "Vàng 18K"

    static {
        setupMaterial(1L, "gold", "Vàng (Gold)");
        setupMaterial(2L, "silver", "Bạc (Silver)");
        setupMaterial(3L, "diamond", "Kim Cương (Diamond)");
        setupMaterial(4L, "platinum", "Bạch Kim (Platinum)");
        setupMaterial(5L, "gemstone", "Đá Quý (Gemstone)");
    }

    private static void setupMaterial(Long id, String slug, String name) {
        MATERIAL_SLUG_MAP.put(slug, id);
        MATERIAL_NAME_MAP.put(id, name);
    }

    // 2. BỘ SƯU TẬP (Collection) -> Map vào category_id_combo
    private static final Map<String, Long> COLLECTION_SLUG_MAP = new HashMap<>();
    private static final Map<Long, String> COLLECTION_NAME_MAP = new HashMap<>();

    static {
        setupCollection(100L, "wedding", "Bộ Sưu Tập Cưới (Wedding)");
        setupCollection(101L, "summer", "Summer Vibes");
        setupCollection(102L, "gift", "Quà Tặng (Gift)");
        setupCollection(103L, "luxury", "Luxury Limited");
    }

    private static void setupCollection(Long id, String slug, String name) {
        COLLECTION_SLUG_MAP.put(slug, id);
        COLLECTION_NAME_MAP.put(id, name);
    }

    // 3. DANH MỤC CHÍNH (Category) - Mapping hỗ trợ tìm kiếm text
    // Bạn có thể mở rộng danh sách này khớp với bảng Category trong DB
    private static final Map<String, Long> CATEGORY_SLUG_MAP = new HashMap<>();
    static {
        CATEGORY_SLUG_MAP.put("necklaces", 1L); // ID 1 trong bảng Category
        CATEGORY_SLUG_MAP.put("earrings", 2L);
        CATEGORY_SLUG_MAP.put("bracelets", 3L);
        CATEGORY_SLUG_MAP.put("rings", 4L);
    }

    // --- LOGIC TÌM KIẾM THÔNG MINH ---
    public List<ProductResponseDTO> searchProducts(String query) {
        String q = query.trim().toLowerCase().replace(" & ", "-").replace(" ", "-"); // chuan hoa slug
        List<Product> products;

        // 1. Check xem user có đang tìm CHẤT LIỆU không? (VD: "gold")
        if (MATERIAL_SLUG_MAP.containsKey(q)) {
            Long materialId = MATERIAL_SLUG_MAP.get(q);
            products = productRepository.findByMaterialId(materialId);
        }
        // 2. Check xem user có đang tìm BỘ SƯU TẬP không? (VD: "wedding")
        else if (COLLECTION_SLUG_MAP.containsKey(q)) {
            Long collectionId = COLLECTION_SLUG_MAP.get(q);
            products = productRepository.findByCollectionId(collectionId);
        }
        // 3. Check xem user có đang tìm DANH MỤC không? (VD: "rings")
        else if (CATEGORY_SLUG_MAP.containsKey(q)) {
            Long categoryId = CATEGORY_SLUG_MAP.get(q);
            products = productRepository.findByCategoryId(categoryId);
        }
        // 4. Các từ khóa đặc biệt khác
        else if (q.equals("san-pham-moi") || q.equals("mon-moi")) {
            products = productRepository.findNewArrivals();
        }
        else if (q.equals("uu-dai") || q.equals("sale")) {
            // Logic tìm hàng giảm giá (nếu có logic giá) hoặc map vào 1 bộ sưu tập Sale
            products = productRepository.searchProductsByKeyword("sale");
        }
        // 5. Tìm kiếm theo tên/mô tả
        else {
            products = productRepository.searchProductsByKeyword(query.trim().toLowerCase());
        }

        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // --- CRUD ---

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

    public Long createProduct(ProductCreateRequest request) {
        // Dùng hàm toProduct() trong DTO để map các trường
        Product product = request.toProduct();
        return productRepository.save(product).getProductId();
    }

    public Product updateProduct(Long id, Product newProductData) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existing.setName(newProductData.getName());
        existing.setDescription(newProductData.getDescription());
        existing.setPrice(newProductData.getPrice());
        existing.setStock(newProductData.getStock());

        // Cập nhật 3 loại Category
        existing.setCategoryId(newProductData.getCategoryId());

        // Map đúng cột tái sử dụng
        existing.setCategory_id_uu_dai(newProductData.getCategory_id_uu_dai()); // Material
        existing.setCategory_id_combo(newProductData.getCategory_id_combo());   // Collection

        if (newProductData.getUrlImg() != null && !newProductData.getUrlImg().isEmpty()) {
            existing.setUrlImg(newProductData.getUrlImg());
        }

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }

    public List<ProductResponseDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductResponseDTO toDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setUrlImg(product.getUrlImg());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());

        // 1. Map Category
        dto.setCategoryId(product.getCategoryId());
        // (Nếu muốn tên Cate, cần repo.findById, tạm thời để tên theo Map nếu có)
        // dto.setCategoryName(...);

        // 2. Map Collection (Combo -> Collection)
        Long colId = product.getCategory_id_combo();
        dto.setCollectionId(colId);
        dto.setCollectionName(COLLECTION_NAME_MAP.getOrDefault(colId, "Khác"));

        // 3. Map Material (UuDai -> Material)
        Long matId = product.getCategory_id_uu_dai();
        dto.setMaterialId(matId);
        dto.setMaterialName(MATERIAL_NAME_MAP.getOrDefault(matId, "Khác"));

        return dto;
    }

    public List<ProductListResponse> getAllProductsForAdmin() {
        return productRepository.findAll().stream().map(product -> {
            ProductListResponse dto = new ProductListResponse();
            dto.setProductId(product.getProductId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());

            // Map thông tin hiển thị cho Admin
            dto.setCategoryId(product.getCategoryId());
            dto.setCategory_id_combo(product.getCategory_id_combo()); // Collection ID
            dto.setCategory_id_uu_dai(product.getCategory_id_uu_dai()); // Material ID

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
}