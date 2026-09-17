package com.iphonefixit.dto;

public class RecentRepairResponse {

    private Long id;
    private String customerName;
    private String phoneNumber;
    private String deviceModel;
    private String status;
    private Double finalRepairCost;

    public RecentRepairResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getFinalRepairCost() {
        return finalRepairCost;
    }

    public void setFinalRepairCost(Double finalRepairCost) {
        this.finalRepairCost = finalRepairCost;
    }
}