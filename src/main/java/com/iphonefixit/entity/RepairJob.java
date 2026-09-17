package com.iphonefixit.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "repair_jobs")
public class RepairJob {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(nullable = false)
    private String deviceModel;

    private String serialNumber;

    @Column(
            nullable = false,
            length = 1000
    )
    private String problem;

    // OPTIONAL
    private String phoneImageUrl;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    private Double finalRepairCost;

    private LocalDate receivedDate;

    private LocalDate deliveryDate;

    @ManyToOne
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;

    public RepairJob() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(
            String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(
            String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(
            String problem) {
        this.problem = problem;
    }

    public String getPhoneImageUrl() {
        return phoneImageUrl;
    }

    public void setPhoneImageUrl(
            String phoneImageUrl) {
        this.phoneImageUrl = phoneImageUrl;
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(
            RepairStatus status) {
        this.status = status;
    }

    public Double getFinalRepairCost() {
        return finalRepairCost;
    }

    public void setFinalRepairCost(
            Double finalRepairCost) {
        this.finalRepairCost =
                finalRepairCost;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(
            LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(
            LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(
            Customer customer) {
        this.customer = customer;
    }
}