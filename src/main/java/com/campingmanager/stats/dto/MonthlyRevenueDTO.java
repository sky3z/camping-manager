package com.campingmanager.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private int year;
    private int month;
    private BigDecimal total;
}
