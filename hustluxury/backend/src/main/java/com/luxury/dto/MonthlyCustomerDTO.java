package com.luxury.dto;

public class MonthlyCustomerDTO {
    private String month;
    private Long customerCount;

    public MonthlyCustomerDTO(Integer monthNumber, Long customerCount) {
        this.month = "T" + monthNumber;
        this.customerCount = (customerCount != null) ? customerCount : 0L;
    }

    public String getMonth() {
        return month;
    }

    public Long getCustomerCount() {
        return customerCount;
    }
}
