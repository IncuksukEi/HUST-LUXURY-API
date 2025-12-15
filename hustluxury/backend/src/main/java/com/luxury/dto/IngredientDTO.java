package com.luxury.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDTO {
    private Long ingredientId;
    private Long productId;
    private String productName;
    private String name;
    private BigDecimal quantity;
    private String unit;
    private String type;
    private BigDecimal price;
}