package com.luxury.dto;

import lombok.Data;
import java.math.BigDecimal;
import com.luxury.entity.Product;

@Data
public class ProductCreateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String urlImg;

    private Long categoryId;

    private Long collectionId;
    private Long materialId;

    private Long category_id_uu_dai;
    private Long category_id_combo;

    public Product toProduct() {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(categoryId);

        product.setCategory_id_combo(collectionId != null ? collectionId : category_id_combo);
        product.setCategory_id_uu_dai(materialId != null ? materialId : category_id_uu_dai);

        product.setUrlImg(urlImg);
        return product;
    }
}