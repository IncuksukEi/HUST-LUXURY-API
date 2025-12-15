package com.luxury.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailResponseDTO {
    private String name;
    private String description;
    private String urlImg;
    private BigDecimal price;
    private Integer quantity;
}
