package com.iphonefixit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iphonefixit.entity.StoreInfo;

public interface StoreInfoRepository
        extends JpaRepository<StoreInfo, Long> {

    Optional<StoreInfo>
        findFirstByOrderByIdAsc();
}