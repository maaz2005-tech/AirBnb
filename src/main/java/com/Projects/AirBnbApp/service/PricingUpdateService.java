package com.Projects.AirBnbApp.service;


import com.Projects.AirBnbApp.entity.Hotel;
import com.Projects.AirBnbApp.entity.HotelMinPrice;
import com.Projects.AirBnbApp.entity.Inventory;
import com.Projects.AirBnbApp.respository.HotelMinPriceRepository;
import com.Projects.AirBnbApp.respository.HotelRepository;
import com.Projects.AirBnbApp.respository.InventoryRepository;
import com.Projects.AirBnbApp.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {

    // Schedular to update the inventory and HotelMinPrice table every hour
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "*/5 0 * * * *")
//    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
        int page = 0;
        int batchSize = 100;

        while (true) {
            Page<Hotel> hotelPage =
                    hotelRepository.findAll(PageRequest.of(page, batchSize));

            if (hotelPage.isEmpty()) {
                break;
            }

            hotelPage.getContent().forEach(this::updateHotelPrices);
            page++;
        }
    }
    private void updateHotelPrices(Hotel hotel){
        log.info("Updating the hotel prices for the hotel Id : {}",hotel.getId());
        LocalDate startDate=LocalDate.now();
        LocalDate endDate=startDate.plusYears(1);
        List<Inventory> inventoryList=inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);
        updateInventoryPrices(inventoryList);
        updateHotelMinPrice(hotel,inventoryList,startDate,endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // compute minimum price per day for the hotel
        Map<LocalDate,BigDecimal> dailyMinPrices=inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice,Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,e->e.getValue().orElse(BigDecimal.ZERO)));
        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelMinPrices=new ArrayList<>();
        dailyMinPrices.forEach((date,prices)->{
            HotelMinPrice hotelMinPrice=hotelMinPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMinPrice(hotel,date));
            hotelMinPrice.setPrice(prices);
            hotelMinPrices.add(hotelMinPrice);
        });
        // save all hotelPrices entities in bulk
        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }

    private  void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice=pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}
