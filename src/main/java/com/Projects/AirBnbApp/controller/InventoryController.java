package com.Projects.AirBnbApp.controller;

import com.Projects.AirBnbApp.dto.InventoryDto;
import com.Projects.AirBnbApp.dto.UpdateInventoryRequestDto;
import com.Projects.AirBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }
    @PatchMapping("rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto){
        inventoryService.updateInventory(roomId,updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }

}
