package com.iphonefixit.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.iphonefixit.dto.CreateSaleRequest;
import com.iphonefixit.entity.Sale;
import com.iphonefixit.service.SaleService;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(
            SaleService saleService) {

        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<Sale> createSale(
            @RequestBody
            CreateSaleRequest request) {

        Sale sale =
                saleService.createSale(
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sale);
    }

    @GetMapping
    public ResponseEntity<List<Sale>>
            getAllSales() {

        return ResponseEntity.ok(
                saleService
                        .getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale>
            getSale(
                    @PathVariable
                    Long id) {

        return ResponseEntity.ok(
                saleService
                        .getSaleById(id));
    }

    @GetMapping("/{id}/bill")
    public ResponseEntity<byte[]>
            downloadBill(
                    @PathVariable
                    Long id) {

        byte[] pdf =
                saleService
                        .generateBill(id);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders
                                .CONTENT_DISPOSITION,
                        "attachment; filename=iphonefixit-sale-"
                                + id
                                + ".pdf")

                .contentType(
                        MediaType
                                .APPLICATION_PDF)

                .body(pdf);
    }

    @ExceptionHandler(
            IllegalArgumentException.class)
    public ResponseEntity<
            Map<String, String>>
            handleBadRequest(
                    IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                e.getMessage()));
    }
}