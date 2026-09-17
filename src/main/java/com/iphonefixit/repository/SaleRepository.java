package com.iphonefixit.repository;

import com.iphonefixit.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository
        extends JpaRepository<Sale, Long> {

    List<Sale> findAllByOrderByIdDesc();
}