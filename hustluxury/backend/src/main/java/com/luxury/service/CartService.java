package com.luxury.service;

import com.luxury.dto.CartDTO;
import com.luxury.entity.Cart;
import com.luxury.entity.Product;
import com.luxury.repository.CartRepository;
import com.luxury.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartDTO> getCartDTOByUserId(Long userId) {
        List<Cart> cartList = cartRepository.findByUserId(userId);
        List<CartDTO> dtoList = new ArrayList<>();

        for (Cart cart : cartList) {
            Product product = cart.getProduct();
            CartDTO dto = new CartDTO(
                    product.getProductId(),
                    product.getName(),
                    product.getDescription(),
                    product.getUrlImg(),
                    cart.getQuantity(),
                    product.getPrice()
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    public void addToCart(Long userId, Long productId, Integer quantity) {
        Optional<Cart> optionalCart = cartRepository.findByUserIdAndProductId(userId, productId);
        Cart cart;
        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
        }
        cartRepository.save(cart);
    }

    public void updateCartQuantities(Long userId, List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("product_id").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            Optional<Cart> optionalCart = cartRepository.findByUserIdAndProductId(userId, productId);
            optionalCart.ifPresent(cart -> {
                cart.setQuantity(quantity);
                cartRepository.save(cart);
            });
        }
    }

    public void removeFromCart(Long userId, Long productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
