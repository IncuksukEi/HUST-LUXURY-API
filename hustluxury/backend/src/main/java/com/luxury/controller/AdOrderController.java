package com.luxury.controller;

import com.luxury.dto.AdOrderRequestDTO;
import com.luxury.dto.AdOrderResponseDTO;
import com.luxury.dto.ProductDTO;
import com.luxury.dto.UserDTO;
import com.luxury.entity.Product;
import com.luxury.repository.AdProductRepository;
import com.luxury.dto.AdOrderDetailDTO;
import com.luxury.service.AdOrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/adorders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdOrderController {

    private final AdOrderService orderService;

    private final AdProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<AdOrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdOrderResponseDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<List<AdOrderDetailDTO>> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }

    @GetMapping("/count-details/{id}")
    public ResponseEntity<Map<String, Integer>> countDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.countDetails(id));
    }

    @GetMapping("/totalPrices/{id}")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPrice(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getTotalPrice(id));
    }

    @PostMapping
    public ResponseEntity<AdOrderResponseDTO> createOrder(@RequestBody AdOrderRequestDTO dto) {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<AdOrderResponseDTO> updateOrder(
            @PathVariable Long orderId,
            @RequestBody AdOrderRequestDTO dto) {

        dto.setOrderId(orderId); // cập nhật lại ID từ URL
        return ResponseEntity.ok(orderService.updateOrder(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user-by-phone/{phone}")
        public ResponseEntity<?> getUserByPhonebyAdmin(@PathVariable String phone) {
            Optional<UserDTO> userOptional = orderService.findUserByPhonebyAdmin(phone);
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }

    @GetMapping("/product-by-name/{name}")
    public ResponseEntity<ProductDTO> searchProductByName(@PathVariable String name) {
        Optional<Product> product = productRepository.findByName(name);
        if (product.isPresent()) {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(product.get().getProductId());
            dto.setName(product.get().getName());
            dto.setPrice(product.get().getPrice());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
