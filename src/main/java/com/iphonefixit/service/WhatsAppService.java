package com.iphonefixit.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iphonefixit.entity.RepairJob;

@Service
public class WhatsAppService {

    private final RepairJobService repairJobService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    public WhatsAppService(RepairJobService repairJobService) {
        this.repairJobService = repairJobService;
    }

    public String generateWhatsAppLink(Long repairId) {

        RepairJob repair =
                repairJobService.getRepairJobById(repairId);

        String phone =
                repair.getCustomer().getPhoneNumber();

        // India country code
        if (!phone.startsWith("91")) {
            phone = "91" + phone;
        }

        String trackingLink =
                frontendUrl + "/track";

        String billLink =
                backendUrl + "/api/bills/" + repairId;

        String message =
                "Hello " + repair.getCustomer().getName() + ",\n\n"
                + "Your iPhoneFixit repair details:\n\n"
                + "Device: " + repair.getDeviceModel() + "\n"
                + "Status: " + repair.getStatus() + "\n"
                + "Final Cost: INR "
                + repair.getFinalRepairCost() + "\n\n"
                + "Track Repair:\n"
                + trackingLink + "\n\n"
                + "Download Bill:\n"
                + billLink + "\n\n"
                + "Thank you for choosing iPhoneFixit.";

        String encodedMessage =
                URLEncoder.encode(
                        message,
                        StandardCharsets.UTF_8);

        return "https://wa.me/"
                + phone
                + "?text="
                + encodedMessage;
    }
}