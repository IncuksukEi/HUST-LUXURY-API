package com.luxury.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonthlySalesDTO {
    private String month;
    private BigDecimal sales;

    public MonthlySalesDTO(String month, BigDecimal sales) {
        this.month = month;
        this.sales = (sales != null)
                ? sales.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public MonthlySalesDTO(Integer monthNumber, BigDecimal salesAmount) {
        this.month = "T" + monthNumber;
        this.sales = (salesAmount != null)
                ? salesAmount.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public MonthlySalesDTO(Long monthNumber, BigDecimal salesAmount) {
        this.month = "T" + monthNumber;
        this.sales = (salesAmount != null)
                ? salesAmount.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getSales() {
        return sales;
    }
}