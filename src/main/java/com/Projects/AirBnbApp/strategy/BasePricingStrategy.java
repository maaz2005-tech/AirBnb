package com.Projects.AirBnbApp.strategy;

import com.Projects.AirBnbApp.entity.Inventory;
import com.Projects.AirBnbApp.entity.Room;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class BasePricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
