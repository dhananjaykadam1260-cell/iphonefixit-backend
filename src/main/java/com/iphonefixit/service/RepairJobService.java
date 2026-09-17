package com.iphonefixit.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iphonefixit.entity.Customer;
import com.iphonefixit.entity.RepairJob;
import com.iphonefixit.entity.RepairStatus;
import com.iphonefixit.repository.CustomerRepository;
import com.iphonefixit.repository.RepairJobRepository;

@Service
public class RepairJobService {

    private final RepairJobRepository repairJobRepository;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    public RepairJobService(
            RepairJobRepository repairJobRepository,
            CustomerRepository customerRepository,
            FileStorageService fileStorageService) {

        this.repairJobRepository = repairJobRepository;
        this.customerRepository = customerRepository;
        this.fileStorageService = fileStorageService;
    }

    public RepairJob createRepairJob(
            Long customerId,
            RepairJob repairJob) {

        Customer customer =
                getCustomer(customerId);

        repairJob.setId(null);
        repairJob.setCustomer(customer);
        repairJob.setStatus(RepairStatus.RECEIVED);
        repairJob.setReceivedDate(LocalDate.now());
        repairJob.setDeliveryDate(null);

        return repairJobRepository.save(repairJob);
    }

    public RepairJob createRepairWithImage(
            Long customerId,
            String deviceModel,
            String serialNumber,
            String problem,
            MultipartFile image) {

        Customer customer =
                getCustomer(customerId);

        RepairJob repairJob =
                new RepairJob();

        repairJob.setCustomer(customer);
        repairJob.setDeviceModel(deviceModel);
        repairJob.setSerialNumber(serialNumber);
        repairJob.setProblem(problem);

        repairJob.setStatus(
                RepairStatus.RECEIVED
        );

        repairJob.setReceivedDate(
                LocalDate.now()
        );

        // IMAGE OPTIONAL
        if (image != null && !image.isEmpty()) {

            String imageUrl =
                    fileStorageService
                            .storeFile(image);

            repairJob.setPhoneImageUrl(imageUrl);

        } else {

            repairJob.setPhoneImageUrl(null);
        }

        return repairJobRepository
                .save(repairJob);
    }

    public List<RepairJob> getAllRepairJobs() {
        return repairJobRepository.findAll();
    }

    public RepairJob getRepairJobById(
            Long id) {

        return repairJobRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Repair job not found"
                        )
                );
    }

    public List<RepairJob> getRepairsByPhoneNumber(
            String phoneNumber) {

        return repairJobRepository
                .findByCustomerPhoneNumber(
                        phoneNumber
                );
    }

    public RepairJob updateStatus(
            Long repairId,
            RepairStatus status) {

        RepairJob repairJob =
                getRepairJobById(repairId);

        repairJob.setStatus(status);

        if (status == RepairStatus.DELIVERED) {

            repairJob.setDeliveryDate(
                    LocalDate.now()
            );

        } else {

            repairJob.setDeliveryDate(null);
        }

        return repairJobRepository
                .save(repairJob);
    }

    public RepairJob updateFinalCost(
            Long repairId,
            Double cost) {

        if (cost == null || cost < 0) {
            throw new RuntimeException(
                    "Invalid repair cost"
            );
        }

        RepairJob repairJob =
                getRepairJobById(repairId);

        repairJob.setFinalRepairCost(cost);

        return repairJobRepository
                .save(repairJob);
    }

    private Customer getCustomer(
            Long customerId) {

        return customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"
                        )
                );
    }
}