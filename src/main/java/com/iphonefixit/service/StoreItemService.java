package com.iphonefixit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iphonefixit.entity.StoreItem;
import com.iphonefixit.repository.StoreItemRepository;

@Service
public class StoreItemService {

    private final StoreItemRepository repository;

    public StoreItemService(
            StoreItemRepository repository) {
        this.repository = repository;
    }

    public List<StoreItem> getPublicItems() {

        return repository
                .findByActiveTrueOrderByIdDesc();
    }

    public List<StoreItem> getAllItems() {

        return repository.findAll();
    }

    public StoreItem getItem(Long id) {

        return repository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item not found"
                        )
                );
    }

    public StoreItem createItem(
            StoreItem item) {

        item.setId(null);

        if (item.getStockQuantity() == null) {
            item.setStockQuantity(0);
        }

        if (item.getPrice() == null) {
            item.setPrice(0.0);
        }

        item.setActive(true);

        return repository.save(item);
    }

    public StoreItem updateItem(
            Long id,
            StoreItem request) {

        StoreItem item =
                getItem(id);

        item.setName(
                request.getName()
        );

        item.setCategory(
                request.getCategory()
        );

        item.setBrand(
                request.getBrand()
        );

        item.setPrice(
                request.getPrice()
        );

        item.setStockQuantity(
                request.getStockQuantity()
        );

        item.setDescription(
                request.getDescription()
        );

        item.setImageUrl(
                request.getImageUrl()
        );

        item.setActive(
                request.isActive()
        );

        return repository.save(item);
    }

    public StoreItem updateStock(
            Long id,
            Integer quantity) {

        if (quantity == null ||
                quantity < 0) {

            throw new RuntimeException(
                    "Invalid stock quantity"
            );
        }

        StoreItem item =
                getItem(id);

        item.setStockQuantity(
                quantity
        );

        return repository.save(item);
    }

    public StoreItem changeStatus(
            Long id,
            boolean active) {

        StoreItem item =
                getItem(id);

        item.setActive(active);

        return repository.save(item);
    }

    public void deleteItem(Long id) {

        StoreItem item =
                getItem(id);

        repository.delete(item);
    }
}