package com.luxury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long productId;
    private String name;
    private String description;
    private String urlImg;
    private Integer quantity;
    private BigDecimal price;
}