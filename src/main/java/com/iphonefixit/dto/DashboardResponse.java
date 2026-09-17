package com.iphonefixit.dto;

import java.util.List;

public class DashboardResponse {

    private double totalRevenue;
    private double todayRevenue;

    private long totalRepairs;
    private long totalCustomers;

    private long receivedRepairs;
    private long checkingRepairs;
    private long repairingRepairs;
    private long readyRepairs;
    private long deliveredRepairs;

    private List<MonthlyRevenueResponse> monthlyRevenue;
    private List<RecentRepairResponse> recentRepairs;

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public long getTotalRepairs() {
        return totalRepairs;
    }

    public void setTotalRepairs(long totalRepairs) {
        this.totalRepairs = totalRepairs;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getReceivedRepairs() {
        return receivedRepairs;
    }

    public void setReceivedRepairs(long receivedRepairs) {
        this.receivedRepairs = receivedRepairs;
    }

    public long getCheckingRepairs() {
        return checkingRepairs;
    }

    public void setCheckingRepairs(long checkingRepairs) {
        this.checkingRepairs = checkingRepairs;
    }

    public long getRepairingRepairs() {
        return repairingRepairs;
    }

    public void setRepairingRepairs(long repairingRepairs) {
        this.repairingRepairs = repairingRepairs;
    }

    public long getReadyRepairs() {
        return readyRepairs;
    }

    public void setReadyRepairs(long readyRepairs) {
        this.readyRepairs = readyRepairs;
    }

    public long getDeliveredRepairs() {
        return deliveredRepairs;
    }

    public void setDeliveredRepairs(long deliveredRepairs) {
        this.deliveredRepairs = deliveredRepairs;
    }

    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(
            List<MonthlyRevenueResponse> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<RecentRepairResponse> getRecentRepairs() {
        return recentRepairs;
    }

    public void setRecentRepairs(
            List<RecentRepairResponse> recentRepairs) {
        this.recentRepairs = recentRepairs;
    }
    
    private double inventoryRevenue;

    public double getInventoryRevenue() {
        return inventoryRevenue;
    }

    public void setInventoryRevenue(double inventoryRevenue) {
        this.inventoryRevenue = inventoryRevenue;
    }
    
}