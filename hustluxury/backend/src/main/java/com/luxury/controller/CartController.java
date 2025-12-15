package com.luxury.controller;

import com.luxury.dto.CartDTO;
import com.luxury.service.CartService;
import com.luxury.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtils;

    // GET: /api/cart - Lấy giỏ hàng của user
    @GetMapping
    public ResponseEntity<List<CartDTO>> getCart(HttpServletRequest request) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        List<CartDTO> cartItems = cartService.getCartDTOByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    // POST: /api/cart/add - Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        Long productId = Long.valueOf(body.get("product_id").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    // POST: /api/cart/update - Cập nhật số lượng sản phẩm trong giỏ
    @PostMapping("/update")
    public ResponseEntity<Void> updateCart(HttpServletRequest request, @RequestBody List<Map<String, Object>> items) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        cartService.updateCartQuantities(userId, items);
        return ResponseEntity.ok().build();
    }

    // DELETE: /api/cart/delete/{id} - Xoá sản phẩm ra khỏi giỏ
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCartItem(HttpServletRequest request, @PathVariable("id") Long productId) {
        Long userId = jwtUtils.getUserIdFromRequest(request);
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }
}