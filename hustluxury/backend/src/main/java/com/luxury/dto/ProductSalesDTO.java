package com.luxury.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductSalesDTO {
    private String name;
    private Long quantitySold;
    private BigDecimal revenue;
}
