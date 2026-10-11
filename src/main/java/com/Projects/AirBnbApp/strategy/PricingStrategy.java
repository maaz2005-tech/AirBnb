package com.Projects.AirBnbApp.strategy;

import com.Projects.AirBnbApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
