package com.luxury.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Sản phẩm không tồn tại với ID: " + id);
    }
}
