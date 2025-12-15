package com.luxury.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luxury.entity.Order.Status;

@Data
public class AdOrderResponseDTO {
    private Long orderId;
    private BigDecimal totalPrice;
    private String orderAddress;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    
    private Status status;
    private Long userId;
    private String fullName;
    private String phone;
    private List<AdOrderDetailDTO> products;
}
