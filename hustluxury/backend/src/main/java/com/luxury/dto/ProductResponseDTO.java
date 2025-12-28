package com.luxury.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private String description;
    private String urlImg;
    private BigDecimal price;
    private Integer stock;

    private Long categoryId;

    private Long collectionId;
    private String collectionName; // Tên hiển thị (VD: "Bộ Sưu Tập Cưới")

    private Long materialId;
    private String materialName;   // Tên hiển thị (VD: "Vàng 18K")
}