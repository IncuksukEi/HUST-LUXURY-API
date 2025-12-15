package com.luxury.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {
    private List<OrderItemDTO> items;
    private String address;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String name;
        private String description;
        private String urlImg;
        private Integer quantity;
        private BigDecimal price;
    }
}
