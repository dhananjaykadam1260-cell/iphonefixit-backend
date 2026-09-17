package com.iphonefixit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iphonefixit.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhoneNumber(String phoneNumber);
}