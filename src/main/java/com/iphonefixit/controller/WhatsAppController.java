package com.iphonefixit.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.iphonefixit.service.WhatsAppService;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = "*")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    public WhatsAppController(
            WhatsAppService whatsAppService) {

        this.whatsAppService = whatsAppService;
    }

    @GetMapping("/share/{repairId}")
    public Map<String, String> shareBill(
            @PathVariable Long repairId) {

        String link =
                whatsAppService.generateWhatsAppLink(repairId);

        return Map.of(
                "whatsappUrl", link
        );
    }
}