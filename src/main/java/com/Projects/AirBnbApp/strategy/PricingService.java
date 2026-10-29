package com.Projects.AirBnbApp.strategy;


import com.Projects.AirBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service

public class PricingService {
    public BigDecimal calculateDynamicPricing(Inventory inventory){
        PricingStrategy pricingStrategy=new BasePricingStrategy();


        // The Below is the Decorator design pattern

        // Apply the additional strategies
        pricingStrategy =new SurgePricingStrategy(pricingStrategy);
        pricingStrategy=new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy=new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy=new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);

    }
    // Return the sum of price of all the inventory
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList){
        return inventoryList.stream()
                .map(inventory -> calculateDynamicPricing(inventory))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
