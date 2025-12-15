package com.luxury.controller;

import com.luxury.dto.MonthlyCustomerDTO;
import com.luxury.dto.MonthlySalesDTO;
import com.luxury.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // Tổng số khách hàng có đơn đã nhận
    @GetMapping("/unique-customers")
    public ResponseEntity<Map<String, Long>> getUniqueCustomersWithReceivedOrders() {
        Long count = analyticsService.getUniqueCustomerCountWithReceivedOrders();

        Map<String, Long> response = new HashMap<>();
        response.put("customerCount", count);

        return ResponseEntity.ok(response);
    }

    // Tổng số đơn hàng
    @GetMapping("/total-orders")
    public ResponseEntity<Map<String, Long>> getTotalOrders() {
        Long orderCount = analyticsService.getTotalOrderCount();

        Map<String, Long> response = new HashMap<>();
        response.put("orderCount", orderCount);

        return ResponseEntity.ok(response);
    }
    // Tổng số sản phẩm đã bán
    @GetMapping("/total-quantity")
    public ResponseEntity<Map<String, Long>> getTotalQuantitySold() {
        Long totalQuantity = analyticsService.getTotalQuantitySold();

        Map<String, Long> response = new HashMap<>();
        response.put("totalQuantitySold", totalQuantity);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/monthly-sales")
    public ResponseEntity<List<MonthlySalesDTO>> getMonthlySales(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {

        List<MonthlySalesDTO> monthlySales = analyticsService.getMonthlySales(year, month);
        if (monthlySales == null || monthlySales.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(monthlySales);
    }

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/monthly-customers")
    public ResponseEntity<List<MonthlyCustomerDTO>> getMonthlyCustomers(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {

        List<MonthlyCustomerDTO> result = analyticsService.getMonthlyCustomerCounts(year, month);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(result);
    }
}
