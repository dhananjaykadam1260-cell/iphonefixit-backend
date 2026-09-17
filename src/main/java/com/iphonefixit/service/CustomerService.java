package com.iphonefixit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iphonefixit.entity.Customer;
import com.iphonefixit.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {

        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new RuntimeException("Customer name is required");
        }

        if (customer.getPhoneNumber() == null ||
                customer.getPhoneNumber().isBlank()) {
            throw new RuntimeException("Phone number is required");
        }

        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with id: " + id));
    }

    public Customer getCustomerByPhone(String phoneNumber) {

        return customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));
    }
}