package com.luxury.controller;

import com.luxury.entity.OrderDetail;
import com.luxury.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orderdetails")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    // Lấy tất cả order details
    @GetMapping
    public List<OrderDetail> getAll() {
        return orderDetailService.getAllOrderDetails();
    }

    // Lấy order detail theo ID
    @GetMapping("/{orderDetailId}")
    public Optional<OrderDetail> getById(@PathVariable Long orderDetailId) {
        return orderDetailService.getOrderDetailById(orderDetailId);
    }

    // Lấy order details theo orderId
    @GetMapping("/order/{orderId}")
    public List<OrderDetail> getByOrderId(@PathVariable Long orderId) {
        return orderDetailService.getOrderDetailsByOrderId(orderId);
    }

    // Lấy order details theo productId
    @GetMapping("/product/{productId}")
    public List<OrderDetail> getByProductId(@PathVariable Long productId) {
        return orderDetailService.getOrderDetailsByProductId(productId);
    }

    // Thêm mới order detail
    @PostMapping
    public OrderDetail create(@RequestBody OrderDetail orderDetail) {
        return orderDetailService.createOrderDetail(orderDetail);
    }

    // Xoá order detail
    @DeleteMapping("/{orderDetailId}")
    public void delete(@PathVariable Long orderDetailId) {
        orderDetailService.deleteOrderDetail(orderDetailId);
    }
}