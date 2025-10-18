package com.sky.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderStatisticsDTO {
    private LocalDate orderDate;
    private BigDecimal totalAmount;
}
