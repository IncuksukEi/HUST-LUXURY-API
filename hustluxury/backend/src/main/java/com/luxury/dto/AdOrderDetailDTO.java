package com.luxury.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdOrderDetailDTO {
    private Long orderDetailId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}