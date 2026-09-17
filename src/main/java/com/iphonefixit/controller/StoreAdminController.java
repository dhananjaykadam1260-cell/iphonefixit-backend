package com.iphonefixit.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.iphonefixit.entity.StoreInfo;
import com.iphonefixit.entity.StoreItem;
import com.iphonefixit.service.StoreInfoService;
import com.iphonefixit.service.StoreItemService;

@RestController
@RequestMapping("/api/admin")
public class StoreAdminController {

    private final StoreInfoService storeInfoService;
    private final StoreItemService storeItemService;

    public StoreAdminController(
            StoreInfoService storeInfoService,
            StoreItemService storeItemService) {

        this.storeInfoService =
                storeInfoService;

        this.storeItemService =
                storeItemService;
    }

    @PutMapping("/store")
    public StoreInfo updateStore(
            @RequestBody StoreInfo storeInfo) {

        return storeInfoService
                .updateStoreInfo(storeInfo);
    }

    @GetMapping("/items")
    public List<StoreItem> allItems() {

        return storeItemService
                .getAllItems();
    }

    @PostMapping("/items")
    public StoreItem createItem(
            @RequestBody StoreItem item) {

        return storeItemService
                .createItem(item);
    }

    @PutMapping("/items/{id}")
    public StoreItem updateItem(
            @PathVariable Long id,
            @RequestBody StoreItem item) {

        return storeItemService
                .updateItem(id, item);
    }

    @PutMapping("/items/{id}/stock")
    public StoreItem updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return storeItemService
                .updateStock(id, quantity);
    }

    @PutMapping("/items/{id}/status")
    public StoreItem changeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return storeItemService
                .changeStatus(id, active);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id) {

        storeItemService.deleteItem(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}