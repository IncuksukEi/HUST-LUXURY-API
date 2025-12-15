package com.luxury.repository;

import com.luxury.dto.ProductSalesDTO;
import com.luxury.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT od FROM OrderDetail od WHERE od.orderId = :orderId")
    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT od FROM OrderDetail od WHERE od.productId = :productId")
    List<OrderDetail> findByProductId(Long productId);

    @Query("""
    SELECT COALESCE(SUM(od.totalPrice), 0)
    FROM OrderDetail od
    JOIN od.order o
    JOIN od.product p
    WHERE o.status <> com.luxury.entity.Order.Status.CANCELLED
      AND (p.category_id_combo = 4)
""")
    BigDecimal getComboRevenue();

    @Query("""
    SELECT new com.luxury.dto.ProductSalesDTO(
        p.name,
        SUM(od.quantity),
        SUM(od.totalPrice)
    )
    FROM OrderDetail od
    JOIN od.product p
    JOIN od.order o
    WHERE o.status <> com.luxury.entity.Order.Status.CANCELLED
    GROUP BY p.productId, p.name
""")
    List<ProductSalesDTO> getProductSalesReport();

    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od JOIN od.order o where o.status <> com.luxury.entity.Order.Status.CANCELLED")
    Long getTotalQuantitySold();
}
