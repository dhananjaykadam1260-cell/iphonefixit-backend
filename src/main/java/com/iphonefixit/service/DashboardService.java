package com.iphonefixit.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.iphonefixit.dto.DashboardResponse;
import com.iphonefixit.dto.MonthlyRevenueResponse;
import com.iphonefixit.dto.RecentRepairResponse;
import com.iphonefixit.entity.RepairJob;
import com.iphonefixit.entity.RepairStatus;
import com.iphonefixit.repository.CustomerRepository;
import com.iphonefixit.repository.RepairJobRepository;
import com.iphonefixit.repository.SaleRepository;

@Service
public class DashboardService {

    private final RepairJobRepository repairJobRepository;
    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;

    public DashboardService(
            RepairJobRepository repairJobRepository,
            CustomerRepository customerRepository,
            SaleRepository saleRepository) {

        this.repairJobRepository = repairJobRepository;
        this.customerRepository = customerRepository;
        this.saleRepository = saleRepository;
    }

    public DashboardResponse getDashboard() {

        DashboardResponse response =
                new DashboardResponse();

        List<RepairJob> deliveredJobs =
                repairJobRepository.findByStatus(
                        RepairStatus.DELIVERED);

        double totalRevenue =
                deliveredJobs.stream()
                        .filter(job ->
                                job.getFinalRepairCost() != null)
                        .mapToDouble(
                                RepairJob::getFinalRepairCost)
                        .sum();

        double todayRevenue =
                deliveredJobs.stream()
                        .filter(job ->
                                job.getDeliveryDate() != null)
                        .filter(job ->
                                job.getDeliveryDate()
                                        .equals(LocalDate.now()))
                        .filter(job ->
                                job.getFinalRepairCost() != null)
                        .mapToDouble(
                                RepairJob::getFinalRepairCost)
                        .sum();

        double inventoryRevenue =
                saleRepository.findAll()
                        .stream()
                        .filter(sale ->
                                sale.getTotalAmount() != null)
                        .mapToDouble(sale ->
                                sale.getTotalAmount()
                                        .doubleValue())
                        .sum();

        response.setTotalRevenue(totalRevenue);

        response.setTodayRevenue(todayRevenue);

        response.setInventoryRevenue(
                inventoryRevenue);

        response.setTotalRepairs(
                repairJobRepository.count());

        response.setTotalCustomers(
                customerRepository.count());

        response.setReceivedRepairs(
                repairJobRepository.countByStatus(
                        RepairStatus.RECEIVED));

        response.setCheckingRepairs(
                repairJobRepository.countByStatus(
                        RepairStatus.CHECKING));

        response.setRepairingRepairs(
                repairJobRepository.countByStatus(
                        RepairStatus.REPAIRING));

        response.setReadyRepairs(
                repairJobRepository.countByStatus(
                        RepairStatus.READY));

        response.setDeliveredRepairs(
                repairJobRepository.countByStatus(
                        RepairStatus.DELIVERED));

        response.setMonthlyRevenue(
                getMonthlyRevenue(deliveredJobs));

        response.setRecentRepairs(
                getRecentRepairs());

        return response;
    }

    private List<MonthlyRevenueResponse> getMonthlyRevenue(
            List<RepairJob> deliveredJobs) {

        List<MonthlyRevenueResponse> result =
                new ArrayList<>();

        YearMonth currentMonth =
                YearMonth.now();

        for (int i = 5; i >= 0; i--) {

            YearMonth month =
                    currentMonth.minusMonths(i);

            double revenue =
                    deliveredJobs.stream()

                            .filter(job ->
                                    job.getDeliveryDate() != null)

                            .filter(job ->
                                    YearMonth.from(
                                            job.getDeliveryDate())
                                            .equals(month))

                            .filter(job ->
                                    job.getFinalRepairCost() != null)

                            .mapToDouble(
                                    RepairJob::getFinalRepairCost)

                            .sum();

            String monthName =
                    month.getMonth()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.ENGLISH);

            result.add(
                    new MonthlyRevenueResponse(
                            monthName,
                            revenue));
        }

        return result;
    }

    private List<RecentRepairResponse> getRecentRepairs() {

        List<RepairJob> jobs =
                repairJobRepository
                        .findTop5ByOrderByIdDesc();

        List<RecentRepairResponse> result =
                new ArrayList<>();

        for (RepairJob job : jobs) {

            RecentRepairResponse dto =
                    new RecentRepairResponse();

            dto.setId(job.getId());

            dto.setCustomerName(
                    job.getCustomer().getName());

            dto.setPhoneNumber(
                    job.getCustomer()
                            .getPhoneNumber());

            dto.setDeviceModel(
                    job.getDeviceModel());

            dto.setStatus(
                    job.getStatus() == null
                            ? "RECEIVED"
                            : job.getStatus()
                                    .toString());

            dto.setFinalRepairCost(
                    job.getFinalRepairCost());

            result.add(dto);
        }

        return result;
    }
}