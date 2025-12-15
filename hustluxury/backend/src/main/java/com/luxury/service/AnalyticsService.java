package com.luxury.service;

import com.luxury.dto.MonthlyCustomerDTO;
import com.luxury.dto.MonthlySalesDTO;
import com.luxury.repository.OrderDetailRepository;
import com.luxury.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public AnalyticsService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Long getUniqueCustomerCountWithReceivedOrders() {
        return orderRepository.countDistinctUsersWithReceivedOrders();
    }

    public Long getTotalOrderCount() {
        return orderRepository.count();
    }

    public Long getTotalQuantitySold() {
        return orderDetailRepository.getTotalQuantitySold();
    }

    public List<MonthlySalesDTO> getMonthlySales(Integer year, Integer month) {
        return orderRepository.findMonthlySales(year, month);
    }
    public List<MonthlyCustomerDTO> getMonthlyCustomerCounts(Integer year, Integer month) {
        return orderRepository.findMonthlyCustomerCounts(year, month);
    }
}
