package com.luxury.repository;

import com.luxury.entity.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdOrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o ORDER BY " +
            "CASE o.status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'CONFIRMED' THEN 2 " +
            "  WHEN 'SHIPPED' THEN 3 " +
            "  WHEN 'RECEIVED' THEN 4 " +
            "  WHEN 'CANCELLED' THEN 5 " +
            "  ELSE 6 END, " +
            "o.orderTime ASC")
    List<Order> findAllSorted();
}
