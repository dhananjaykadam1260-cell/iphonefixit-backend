package com.iphonefixit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.iphonefixit.entity.StoreInfo;
import com.iphonefixit.entity.StoreItem;
import com.iphonefixit.service.StoreInfoService;
import com.iphonefixit.service.StoreItemService;

@RestController
@RequestMapping("/api")
public class StoreController {

    private final StoreInfoService storeInfoService;
    private final StoreItemService storeItemService;

    public StoreController(
            StoreInfoService storeInfoService,
            StoreItemService storeItemService) {

        this.storeInfoService =
                storeInfoService;

        this.storeItemService =
                storeItemService;
    }

    @GetMapping("/store")
    public StoreInfo getStore() {

        return storeInfoService
                .getStoreInfo();
    }

    @GetMapping("/items")
    public List<StoreItem> getItems() {

        return storeItemService
                .getPublicItems();
    }

    @GetMapping("/items/{id}")
    public StoreItem getItem(
            @PathVariable Long id) {

        return storeItemService
                .getItem(id);
    }
}