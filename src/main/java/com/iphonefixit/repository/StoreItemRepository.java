package com.iphonefixit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iphonefixit.entity.StoreItem;

public interface StoreItemRepository
        extends JpaRepository<StoreItem, Long> {

    List<StoreItem> findByActiveTrueOrderByIdDesc();
}