package com.luxury.service;

import com.luxury.dto.ProductSalesDTO;
import com.luxury.entity.*;
import com.luxury.repository.*;

import jakarta.transaction.Transactional;

import com.luxury.dto.OrderResponseDTO;
import com.luxury.dto.OrderDetailResponseDTO;
import com.luxury.dto.OrderManagementDTO;
import com.luxury.dto.OrderRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public void placeOrder(Long userId, OrderRequestDTO orderRequest) {
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IllegalStateException("Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm trước khi đặt hàng.");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> detailList = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        String orderAddress = (orderRequest.getAddress() != null && !orderRequest.getAddress().trim().isEmpty())
                ? orderRequest.getAddress()
                : user.getAddress();

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderAddress(orderAddress);
        order.setOrderTime(LocalDateTime.now());

        for (OrderRequestDTO.OrderItemDTO item : orderRequest.getItems()) {
            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

            // Kiểm tra tồn kho
            if (quantity > product.getStock()) {
                throw new IllegalStateException("Sản phẩm '" + product.getName() + "' không đủ hàng trong kho. Hiện còn " + product.getStock());
            }

            // Cập nhật lại tồn kho
            product.setStock(product.getStock() - quantity);
            product.setSoldQuantity(product.getSoldQuantity() + quantity);
            productRepository.save(product); // lưu cập nhật

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(itemTotal);

            OrderDetail detail = new OrderDetail();
            detail.setProductId(productId);
            detail.setQuantity(quantity);
            detail.setTotalPrice(itemTotal);
            detailList.add(detail);
        }

        order.setTotalPrice(total);
        Order savedOrder = orderRepository.save(order);

        for (OrderDetail detail : detailList) {
            detail.setOrderId(savedOrder.getOrderId());
            orderDetailRepository.save(detail);
        }

        cartRepository.deleteByUserId(userId);
    }



    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    @Transactional
    public List<OrderResponseDTO> getOrdersWithDetailsByUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponseDTO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getOrderId());
            dto.setTotalPrice(order.getTotalPrice());

            dto.setOrderAddress(order.getOrderAddress());
            dto.setOrderTime(order.getOrderTime());
            dto.setStatus(order.getStatus());

            List<OrderDetailResponseDTO> productList = new ArrayList<>();

            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();

                OrderDetailResponseDTO pDto = new OrderDetailResponseDTO();
                pDto.setName(product.getName());
                pDto.setDescription(product.getDescription());
                pDto.setUrlImg(product.getUrlImg());
                pDto.setPrice(product.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
                pDto.setQuantity(detail.getQuantity());

                productList.add(pDto);
            }

            dto.setProducts(productList);
            result.add(dto);
        }

        return result;
    }
    // Lọc tổng số tiền nhận được
    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }
    // Lọc tổng số tiền đơn hàng bị hủy
    // public Map<String, Object> getCancelledOrdersStats() {
    //     Object[] result = orderRepository.getCancelledOrdersStats();
    //     Long totalCancelledOrders = (Long) result[0];
    //     BigDecimal cancelledValue = (BigDecimal) result[1];

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("totalCancelledOrders", totalCancelledOrders);
    //     response.put("cancelledValue", cancelledValue);
    //     return response;
    // }
    public BigDecimal getCancelledOrdersTotal() {
        return orderRepository.getCancelledOrdersTotal();
    }

    // Doanh thu combo
    public BigDecimal getComboRevenue() {
        return orderDetailRepository.getComboRevenue();
    }
    // Sản phẩm kèm theo doanh thu
    public List<ProductSalesDTO> getProductSalesReport() {
        return orderDetailRepository.getProductSalesReport();
    }

    public void updateOrderStatus(Long orderId, String statusStr, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Order.Status newStatus;
        try {
            newStatus = Order.Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusStr);
        }

        Order.Status currentStatus = order.getStatus();

        boolean isAdmin = user.getRole().equals(User.Role.ADMIN);

        if (isAdmin) {
            order.setStatus(newStatus);
        } else {
            if (!order.getUserId().equals(user.getUserId())) {
                throw new SecurityException("You are not allowed to modify this order.");
            }

            if ((currentStatus == Order.Status.PENDING && newStatus == Order.Status.CANCELLED) ||
                    (currentStatus == Order.Status.SHIPPED && newStatus == Order.Status.RECEIVED)) {
                order.setStatus(newStatus);
            } else {
                throw new IllegalArgumentException("You are not allowed to change status from " +
                        currentStatus + " to " + newStatus);
            }
        }

        orderRepository.save(order);
    }
    public double getCancelledOrderRate() {
        long totalOrders = orderRepository.countTotalOrders();
        if (totalOrders == 0) return 0.0;

        long cancelledOrders = orderRepository.countCancelledOrders();
        return (double) cancelledOrders / totalOrders;
    }
    public List<OrderManagementDTO> getAllOrdersForManagement() {
        List<Object[]> rawData = orderRepository.findOrdersWithUserInfo();

        return rawData.stream().map(row -> {
            Long orderId = (Long) row[0];
            String fullName = (String) row[1];
            com.luxury.entity.Order.Status status = (com.luxury.entity.Order.Status) row[2];
            Double price = (row[3] instanceof BigDecimal)
                    ? ((BigDecimal) row[3]).doubleValue()
                    : (Double) row[3];

            return new OrderManagementDTO(orderId, fullName, status, price);
        }).toList();
    }

}