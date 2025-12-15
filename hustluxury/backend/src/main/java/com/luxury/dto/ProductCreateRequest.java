// package com.luxury.dto;

// import lombok.Data;
// import java.math.BigDecimal;
// import com.luxury.entity.Product;

// @Data
// public class ProductCreateRequest {
//     private String name;
//     private String description;
//     private BigDecimal price;
//     private Long categoryId;
//     private Integer stock;
//     private String urlImg;
//     private Long category_id_uu_dai;
//     private Long category_id_combo;

//     public Product toProduct() {
//         Product product = new Product();
//         product.setName(name);
//         product.setDescription(description);
//         product.setPrice(price);
//         product.setStock(stock);
//         product.setCategoryId(categoryId);
//         product.setCategory_id_uu_dai(category_id_uu_dai);
//         product.setCategory_id_combo(category_id_combo);
//         product.setUrlImg(urlImg);
//         return product;
//     }
// }
package com.luxury.dto;

import lombok.Data;
import java.math.BigDecimal;
import com.luxury.entity.Product;

@Data
public class ProductCreateRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Integer stock;
    private String urlImg;
    private Long category_id_uu_dai;
    private Long category_id_combo;

    public Product toProduct() {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(categoryId);
        product.setCategory_id_uu_dai(category_id_uu_dai);
        product.setCategory_id_combo(category_id_combo);
        product.setUrlImg(urlImg);
        return product;
    }
}
