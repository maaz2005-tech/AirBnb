package com.Projects.AirBnbApp.dto;


import com.Projects.AirBnbApp.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal surgeFactor;
    private Boolean closed;
}
