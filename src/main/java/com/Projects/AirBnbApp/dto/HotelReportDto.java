package com.Projects.AirBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelReportDto {
    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgRevenue;
}
