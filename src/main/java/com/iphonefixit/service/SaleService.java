package com.iphonefixit.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iphonefixit.dto.CreateSaleRequest;
import com.iphonefixit.dto.SaleItemRequest;
import com.iphonefixit.entity.Sale;
import com.iphonefixit.entity.SaleItem;
import com.iphonefixit.entity.StoreItem;
import com.iphonefixit.repository.SaleRepository;
import com.iphonefixit.repository.StoreItemRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final StoreItemRepository storeItemRepository;

    public SaleService(
            SaleRepository saleRepository,
            StoreItemRepository storeItemRepository) {

        this.saleRepository = saleRepository;
        this.storeItemRepository = storeItemRepository;
    }

    @Transactional
    public Sale createSale(CreateSaleRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Sale request is required");
        }

        if (request.getCustomerName() == null ||
                request.getCustomerName().isBlank()) {

            throw new IllegalArgumentException(
                    "Customer name is required");
        }

        if (request.getPhoneNumber() == null ||
                request.getPhoneNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Phone number is required");
        }

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one item is required");
        }

        Sale sale = new Sale();

        sale.setInvoiceNumber(
                generateInvoiceNumber());

        sale.setCustomerName(
                request.getCustomerName().trim());

        sale.setPhoneNumber(
                request.getPhoneNumber().trim());

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        for (SaleItemRequest requestItem :
                request.getItems()) {

            if (requestItem.getItemId() == null) {
                throw new IllegalArgumentException(
                        "Item ID is required");
            }

            if (requestItem.getQuantity() == null ||
                    requestItem.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Quantity must be greater than 0");
            }

            StoreItem storeItem =
                    storeItemRepository
                            .findById(
                                    requestItem.getItemId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Inventory item not found: "
                                                    + requestItem.getItemId()
                                    )
                            );

            if (!storeItem.isActive()) {
                throw new IllegalArgumentException(
                        storeItem.getName()
                                + " is currently disabled");
            }

            int availableStock =
                    storeItem.getStockQuantity() == null
                            ? 0
                            : storeItem.getStockQuantity();

            if (availableStock <
                    requestItem.getQuantity()) {

                throw new IllegalArgumentException(
                        "Not enough stock for "
                                + storeItem.getName()
                                + ". Available: "
                                + availableStock);
            }

            if (storeItem.getPrice() == null) {
                throw new IllegalArgumentException(
                        "Price is not set for "
                                + storeItem.getName());
            }

            BigDecimal unitPrice =
                    BigDecimal.valueOf(
                                    storeItem.getPrice())
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP);

            BigDecimal lineTotal =
                    unitPrice.multiply(
                            BigDecimal.valueOf(
                                    requestItem.getQuantity()));

            SaleItem saleItem =
                    new SaleItem();

            saleItem.setStoreItemId(
                    storeItem.getId());

            saleItem.setItemName(
                    storeItem.getName());

            saleItem.setUnitPrice(
                    unitPrice);

            saleItem.setQuantity(
                    requestItem.getQuantity());

            saleItem.setLineTotal(
                    lineTotal);

            sale.addItem(saleItem);

            storeItem.setStockQuantity(
                    availableStock -
                            requestItem.getQuantity());

            storeItemRepository.save(
                    storeItem);

            totalAmount =
                    totalAmount.add(
                            lineTotal);
        }

        sale.setTotalAmount(
                totalAmount.setScale(
                        2,
                        RoundingMode.HALF_UP));

        return saleRepository.save(sale);
    }

    @Transactional(readOnly = true)
    public List<Sale> getAllSales() {

        List<Sale> sales =
                saleRepository
                        .findAllByOrderByIdDesc();

        sales.forEach(
                sale ->
                        sale.getItems().size());

        return sales;
    }

    @Transactional(readOnly = true)
    public Sale getSaleById(Long id) {

        Sale sale =
                saleRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Sale not found"));

        sale.getItems().size();

        return sale;
    }

    @Transactional(readOnly = true)
    public byte[] generateBill(Long saleId) {

        Sale sale =
                getSaleById(saleId);

        try {

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            Document document =
                    new Document();

            document.setMargins(
                    35,
                    35,
                    30,
                    30);

            PdfWriter.getInstance(
                    document,
                    output);

            document.open();

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            22,
                            new Color(
                                    30,
                                    41,
                                    59));

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10,
                            Color.DARK_GRAY);

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            Color.DARK_GRAY);

            Paragraph title =
                    new Paragraph(
                            "iPhoneFixit",
                            titleFont);

            title.setAlignment(
                    Element.ALIGN_CENTER);

            document.add(title);

            Paragraph subtitle =
                    new Paragraph(
                            "Inventory Sales Invoice",
                            normalFont);

            subtitle.setAlignment(
                    Element.ALIGN_CENTER);

            document.add(subtitle);

            document.add(
                    new Paragraph(" "));

            document.add(
                    new Paragraph(
                            "Invoice: "
                                    + sale.getInvoiceNumber(),
                            boldFont));

            document.add(
                    new Paragraph(
                            "Customer: "
                                    + sale.getCustomerName(),
                            normalFont));

            document.add(
                    new Paragraph(
                            "Phone: "
                                    + sale.getPhoneNumber(),
                            normalFont));

            if (sale.getSaleDate() != null) {

                String date =
                        sale.getSaleDate()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "dd MMM yyyy hh:mm a"));

                document.add(
                        new Paragraph(
                                "Date: " + date,
                                normalFont));
            }

            document.add(
                    new Paragraph(" "));

            PdfPTable table =
                    new PdfPTable(4);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{
                            3f,
                            1f,
                            1.5f,
                            1.5f
                    });

            addHeader(
                    table,
                    "Item");

            addHeader(
                    table,
                    "Qty");

            addHeader(
                    table,
                    "Price");

            addHeader(
                    table,
                    "Total");

            for (SaleItem item :
                    sale.getItems()) {

                addCell(
                        table,
                        item.getItemName());

                addCell(
                        table,
                        String.valueOf(
                                item.getQuantity()));

                addCell(
                        table,
                        "Rs. "
                                + item.getUnitPrice());

                addCell(
                        table,
                        "Rs. "
                                + item.getLineTotal());
            }

            document.add(table);

            document.add(
                    new Paragraph(" "));

            Paragraph total =
                    new Paragraph(
                            "Total Amount: Rs. "
                                    + sale.getTotalAmount(),
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    14,
                                    new Color(
                                            37,
                                            99,
                                            235)));

            total.setAlignment(
                    Element.ALIGN_RIGHT);

            document.add(total);

            document.add(
                    new Paragraph(" "));

            Paragraph thanks =
                    new Paragraph(
                            "Thank you for shopping with iPhoneFixit.",
                            boldFont);

            thanks.setAlignment(
                    Element.ALIGN_CENTER);

            document.add(thanks);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to generate sales bill",
                    e);
        }
    }

    private String generateInvoiceNumber() {

        String random =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 5)
                        .toUpperCase();

        return "IFX-SALE-"
                + System.currentTimeMillis()
                + "-"
                + random;
    }

    private void addHeader(
            PdfPTable table,
            String text) {

        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        10,
                        Color.WHITE);

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text,
                                font));

        cell.setBackgroundColor(
                new Color(
                        37,
                        99,
                        235));

        cell.setPadding(8);

        cell.setBorder(
                Rectangle.NO_BORDER);

        table.addCell(cell);
    }

    private void addCell(
            PdfPTable table,
            String text) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text == null
                                        ? "-"
                                        : text));

        cell.setPadding(8);

        table.addCell(cell);
    }
}