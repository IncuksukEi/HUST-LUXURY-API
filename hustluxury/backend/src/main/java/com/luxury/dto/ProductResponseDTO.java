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
}
