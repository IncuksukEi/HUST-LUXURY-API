package com.luxury.controller;

import com.luxury.dto.OrderManagementDTO;
import com.luxury.dto.OrderRequestDTO;
import com.luxury.dto.OrderResponseDTO;
import com.luxury.entity.User;
import com.luxury.service.OrderService;
import com.luxury.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    // POST /api/orders - Tạo đơn hàng
    @PostMapping
    public ResponseEntity<?> placeOrder(HttpServletRequest request, @RequestBody OrderRequestDTO orderRequest) {
        Long userId = jwtUtil.getUserIdFromRequest(request);

        try {
            orderService.placeOrder(userId, orderRequest);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        List<OrderResponseDTO> orders = orderService.getOrdersWithDetailsByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        String status = body.get("status");
        User user = jwtUtil.extractUserFromRequest(request);

        try {
            orderService.updateOrderStatus(orderId, status, user);
            return ResponseEntity.ok().body(Map.of("message", "Order status updated successfully"));
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/management")
    public ResponseEntity<List<OrderManagementDTO>> getOrdersForManagement() {
        List<OrderManagementDTO> orders = orderService.getAllOrdersForManagement();
        return ResponseEntity.ok(orders);
    }
}