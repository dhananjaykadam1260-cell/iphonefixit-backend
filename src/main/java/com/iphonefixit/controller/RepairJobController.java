package com.iphonefixit.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.iphonefixit.entity.RepairJob;
import com.iphonefixit.entity.RepairStatus;
import com.iphonefixit.service.RepairJobService;

@RestController
@RequestMapping("/api/repairs")
public class RepairJobController {

    private final RepairJobService repairJobService;

    public RepairJobController(
            RepairJobService repairJobService) {
        this.repairJobService = repairJobService;
    }

    // Normal JSON repair
    @PostMapping("/customer/{customerId}")
    public RepairJob createRepairJob(
            @PathVariable Long customerId,
            @RequestBody RepairJob repairJob) {

        return repairJobService
                .createRepairJob(customerId, repairJob);
    }

    // Repair with OPTIONAL photo
    @PostMapping(
            value = "/customer/{customerId}/with-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public RepairJob createRepairWithImage(
            @PathVariable Long customerId,

            @RequestParam String deviceModel,

            @RequestParam(required = false)
            String serialNumber,

            @RequestParam String problem,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image) {

        return repairJobService.createRepairWithImage(
                customerId,
                deviceModel,
                serialNumber,
                problem,
                image
        );
    }

    @GetMapping
    public List<RepairJob> getAllRepairs() {
        return repairJobService.getAllRepairJobs();
    }

    @GetMapping("/{id}")
    public RepairJob getRepairById(
            @PathVariable Long id) {

        return repairJobService
                .getRepairJobById(id);
    }

    @GetMapping("/phone/{phoneNumber}")
    public List<RepairJob> getByPhoneNumber(
            @PathVariable String phoneNumber) {

        return repairJobService
                .getRepairsByPhoneNumber(phoneNumber);
    }

    @PutMapping("/{id}/status")
    public RepairJob updateStatus(
            @PathVariable Long id,
            @RequestParam RepairStatus status) {

        return repairJobService
                .updateStatus(id, status);
    }

    @PutMapping("/{id}/cost")
    public RepairJob updateCost(
            @PathVariable Long id,
            @RequestParam Double cost) {

        return repairJobService
                .updateFinalCost(id, cost);
    }
}