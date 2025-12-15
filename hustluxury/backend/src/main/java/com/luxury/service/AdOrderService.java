package com.luxury.service;

import com.luxury.dto.AdOrderDetailDTO;
import com.luxury.dto.AdOrderRequestDTO;
import com.luxury.dto.AdOrderResponseDTO;
import com.luxury.dto.ProductDTO;
import com.luxury.dto.UserDTO;
import com.luxury.entity.*;
import com.luxury.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdOrderService {
    private final AdOrderRepository orderRepository;
    private final AdOrderDetailRepository orderDetailRepository;
    private final AdProductRepository productRepository;
    private final AdUserRepository userRepository;

    public List<AdOrderResponseDTO> getAllOrders() {
        return orderRepository.findAllSorted().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AdOrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id)
            .map(this::convertToDto)
            .orElse(null);
    }

    public List<AdOrderDetailDTO> getOrderDetails(Long id) {
        return orderDetailRepository.findByOrderId(id).stream()
            .map(this::convertDetailToDto)
            .collect(Collectors.toList());
    }

    public Map<String, Integer> countDetails(Long id) {
        int count = orderDetailRepository.findByOrderId(id).size();
        return Map.of("count-details", count);
    }

    public Map<String, BigDecimal> getTotalPrice(Long orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        BigDecimal total = details.stream()
            .map(od -> {
                Product product = productRepository.findById(od.getProductId()).orElse(null);
                return product != null ? product.getPrice().multiply(BigDecimal.valueOf(od.getQuantity())) : BigDecimal.ZERO;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalPrice", total);
    }

    @Transactional
public AdOrderResponseDTO createOrder(AdOrderRequestDTO dto) {
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    Order order = new Order();
    order.setUserId(dto.getUserId());
    order.setUser(user); // 🔧 Gán User object
    order.setOrderAddress(dto.getOrderAddress());
    order.setOrderTime(LocalDateTime.now());
    order.setStatus(Order.Status.PENDING);
    order.setOrderDetails(new ArrayList<>());
    Order savedOrder = orderRepository.save(order);

    BigDecimal total = BigDecimal.ZERO;
    for (AdOrderDetailDTO item : dto.getProducts()) {
        Product product = productRepository.findById(item.getProductId()).orElseThrow();
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(savedOrder.getOrderId());
        detail.setProductId(product.getProductId());
        detail.setQuantity(item.getQuantity());
        BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        detail.setTotalPrice(itemTotal);
        orderDetailRepository.save(detail);
        total = total.add(itemTotal);
    }

    savedOrder.setTotalPrice(total);
    return convertToDto(orderRepository.save(savedOrder));
}

    @Transactional
    public AdOrderResponseDTO updateOrder(AdOrderRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId()).orElseThrow();
        
        // Optional: nếu cho phép cập nhật user
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUserId(dto.getUserId());
        order.setUser(user); // 🔧 Gán lại User object
        order.setOrderAddress(dto.getOrderAddress());
        order.setStatus(dto.getStatus());
        orderDetailRepository.deleteByOrderId(order.getOrderId());

        BigDecimal total = BigDecimal.ZERO;
        for (AdOrderDetailDTO item : dto.getProducts()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getOrderId());
            detail.setProductId(product.getProductId());
            detail.setQuantity(item.getQuantity());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            detail.setTotalPrice(itemTotal);
            orderDetailRepository.save(detail);
            total = total.add(itemTotal);
        }

        order.setTotalPrice(total);
        return convertToDto(orderRepository.save(order));
    }

    

    @Transactional
    public void deleteOrder(Long id) {
        orderDetailRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
    }

    // -------- Helper Methods --------

   private AdOrderResponseDTO convertToDto(Order order) {
    AdOrderResponseDTO dto = new AdOrderResponseDTO();
    dto.setOrderId(order.getOrderId());
    dto.setOrderAddress(order.getOrderAddress());
    dto.setOrderTime(order.getOrderTime());
    dto.setStatus(order.getStatus());
    dto.setUserId(order.getUserId());

    User user = userRepository.findById(order.getUserId()).orElse(null);
    if (user != null) {
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone()); // ✅ Gán phone từ User repo
    } else {
        dto.setPhone(null); // hoặc dto.setPhone("Unknown");
    }

    List<AdOrderDetailDTO> detailDTOs = orderDetailRepository.findByOrderId(order.getOrderId()).stream()
        .map(this::convertDetailToDto)
        .collect(Collectors.toList());

    dto.setProducts(detailDTOs);

    BigDecimal total = detailDTOs.stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    dto.setTotalPrice(total);

    return dto;
}


    private AdOrderDetailDTO convertDetailToDto(OrderDetail detail) {
        AdOrderDetailDTO dto = new AdOrderDetailDTO();
        dto.setOrderDetailId(detail.getOrderDetailId());
        dto.setProductId(detail.getProductId());
        dto.setQuantity(detail.getQuantity());

        Product product = productRepository.findById(detail.getProductId()).orElse(null);
        if (product != null) {
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
        }

        return dto;
    }
     public Optional<UserDTO> findUserByPhonebyAdmin(String phone) {
        return userRepository.findByPhone(phone)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setFullName(user.getFullName());
                    dto.setAddress(user.getAddress());
                    return dto;
                });
    }

    public Optional<ProductDTO> findProductByNamebyAdmin(String name) {
        return productRepository.findByName(name)
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setProductId(product.getProductId());
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    return dto;
                });
    }
}
