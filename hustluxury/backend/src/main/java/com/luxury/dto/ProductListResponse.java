package com.luxury.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListResponse {
    private Long productId;
    private String name;
    private String description;          // thêm mô tả sản phẩm
    private BigDecimal price;
    private Long categoryId;
    private Long category_id_combo;        // thêm category_id_combo
    private Long category_id_uu_dai;        // thêm category_id_uu_dai
    private Integer stock;
    private Integer soldQuantity;
    private String urlImg;
}
