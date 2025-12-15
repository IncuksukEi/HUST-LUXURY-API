package com.luxury.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luxury.entity.Order.Status;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal totalPrice;
    private String orderAddress;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    private Status status;
    private List<OrderDetailResponseDTO> products;
}
