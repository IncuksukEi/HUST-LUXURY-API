package com.luxury.dto;

import com.luxury.entity.Order.Status;


public class OrderManagementDTO {
    private Long orderId;
    private String fullName;
    private Status status;
    private Double price;
    public OrderManagementDTO(Long orderId, String fullName, Status status, Double price) {
        this.orderId = orderId;
        this.fullName = fullName;
        this.status = status;
        this.price = price;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getFullName() {
        return fullName;
    }

    public Status getStatus() {
        return status;
    }

    public Double getPrice() {
        return price;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
