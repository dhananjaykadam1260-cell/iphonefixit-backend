package com.iphonefixit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.iphonefixit.entity.Customer;
import com.iphonefixit.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService) {

        this.customerService = customerService;
    }

    @PostMapping
    public Customer addCustomer(
            @RequestBody Customer customer) {

        return customerService.addCustomer(customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {

        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(
            @PathVariable Long id) {

        return customerService.getCustomerById(id);
    }

    @GetMapping("/phone/{phoneNumber}")
    public Customer getCustomerByPhone(
            @PathVariable String phoneNumber) {

        return customerService
                .getCustomerByPhone(phoneNumber);
    }
}