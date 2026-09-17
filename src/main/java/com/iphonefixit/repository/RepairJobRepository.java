package com.iphonefixit.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iphonefixit.entity.RepairJob;
import com.iphonefixit.entity.RepairStatus;

public interface RepairJobRepository extends JpaRepository<RepairJob, Long> {

    List<RepairJob> findByCustomerPhoneNumber(String phoneNumber);

    long countByStatus(RepairStatus status);

    List<RepairJob> findByStatus(RepairStatus status);

    List<RepairJob> findByStatusAndDeliveryDate(
            RepairStatus status,
            LocalDate deliveryDate);

    List<RepairJob> findTop5ByOrderByIdDesc();
}