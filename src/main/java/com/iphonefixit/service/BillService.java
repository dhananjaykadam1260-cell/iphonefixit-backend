package com.iphonefixit.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.iphonefixit.entity.RepairJob;
import com.lowagie.text.Chunk;
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

import java.awt.Color;

@Service
public class BillService {

    // Brand colors
    private static final Color BRAND_DARK   = new Color(30, 41, 59);   // slate-800
    private static final Color BRAND_ACCENT = new Color(37, 99, 235);  // blue-600
    private static final Color ROW_ALT      = new Color(241, 245, 249);// slate-100
    private static final Color BORDER_GRAY  = new Color(203, 213, 225);// slate-300

    private final RepairJobService repairJobService;

    public BillService(RepairJobService repairJobService) {
        this.repairJobService = repairJobService;
    }

    public byte[] generateBill(Long repairId) {

        RepairJob repair = repairJobService.getRepairJobById(repairId);

        if (repair.getFinalRepairCost() == null) {
            throw new RuntimeException(
                    "Final repair cost must be added before generating bill");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            document.setMargins(36, 36, 20, 36);

            PdfWriter.getInstance(document, outputStream);
            document.open();

            // ---------- Fonts ----------
            Font titleFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND_DARK);
            Font taglineFont  = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.WHITE);
            Font headingFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BRAND_DARK);
            Font labelFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BRAND_DARK);
            Font valueFont    = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font totalLabel   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font totalValue   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font footerFont   = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);
            Font metaFont     = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);

            // ---------- Header band ----------
            PdfPTable headerBand = new PdfPTable(1);
            headerBand.setWidthPercentage(100);
            PdfPCell bandCell = new PdfPCell();
            bandCell.setBackgroundColor(BRAND_DARK);
            bandCell.setBorder(Rectangle.NO_BORDER);
            bandCell.setPadding(16);

            Paragraph brand = new Paragraph("iPhoneFixit",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE));
            brand.setAlignment(Element.ALIGN_CENTER);
            bandCell.addElement(brand);

            Paragraph tagline = new Paragraph("Repair Service Bill", taglineFont);
            tagline.setAlignment(Element.ALIGN_CENTER);
            bandCell.addElement(tagline);

            headerBand.addCell(bandCell);
            document.add(headerBand);

            document.add(new Paragraph(" "));

            // ---------- Bill meta row ----------
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{1f, 1f});

            PdfPCell billNoCell = borderlessCell(
                    "Bill No: IFX-" + repair.getId(), headingFont, Element.ALIGN_LEFT);
            String today = new SimpleDateFormat("dd MMM yyyy").format(new Date());
            PdfPCell dateCell = borderlessCell("Date: " + today, metaFont, Element.ALIGN_RIGHT);

            metaTable.addCell(billNoCell);
            metaTable.addCell(dateCell);
            document.add(metaTable);

            // Accent divider line
            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            PdfPCell dividerCell = new PdfPCell();
            dividerCell.setFixedHeight(2f);
            dividerCell.setBackgroundColor(BRAND_ACCENT);
            dividerCell.setBorder(Rectangle.NO_BORDER);
            divider.addCell(dividerCell);
            document.add(divider);

            document.add(new Paragraph(" "));

            // ---------- Customer details ----------
            document.add(new Paragraph("Customer: " + repair.getCustomer().getName(), valueFont));
            document.add(new Paragraph("Phone Number: " + repair.getCustomer().getPhoneNumber(), valueFont));

            document.add(new Paragraph(" "));

            // ---------- Details table ----------
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 1.4f});

            addRow(table, "Device", repair.getDeviceModel(), labelFont, valueFont, true);
            addRow(table, "Serial Number",
                    repair.getSerialNumber() == null ? "-" : repair.getSerialNumber(),
                    labelFont, valueFont, false);
            addRow(table, "Problem", repair.getProblem(), labelFont, valueFont, true);
            addRow(table, "Received Date",
                    repair.getReceivedDate() == null ? "-" : repair.getReceivedDate().toString(),
                    labelFont, valueFont, false);
            addRow(table, "Repair Status", repair.getStatus().toString(), labelFont, valueFont, true);

            if (repair.getDeliveryDate() != null) {
                addRow(table, "Delivery Date", repair.getDeliveryDate().toString(),
                        labelFont, valueFont, false);
            }

            document.add(table);

            document.add(new Paragraph(" "));

            // ---------- Total cost banner ----------
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{1f, 1f});

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Final Repair Cost", totalLabel));
            totalLabelCell.setBackgroundColor(BRAND_ACCENT);
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setPadding(10);
            totalLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell totalValueCell = new PdfPCell(
                    new Phrase("Rs. " + repair.getFinalRepairCost(), totalValue));
            totalValueCell.setBackgroundColor(BRAND_ACCENT);
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setPadding(10);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            totalTable.addCell(totalLabelCell);
            totalTable.addCell(totalValueCell);
            document.add(totalTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // ---------- Footer ----------
            Paragraph thanks = new Paragraph("Thank you for choosing iPhoneFixit.",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BRAND_DARK));
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            Paragraph footerNote = new Paragraph(
                    "This is a computer-generated bill and does not require a signature.",
                    footerFont);
            footerNote.setAlignment(Element.ALIGN_CENTER);
            footerNote.setSpacingBefore(4f);
            document.add(footerNote);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Unable to generate PDF bill", e);
        }
    }

    private PdfPCell borderlessCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private void addRow(PdfPTable table, String label, String value,
                         Font labelFont, Font valueFont, boolean shaded) {

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(8);
        labelCell.setBorderColor(BORDER_GRAY);
        labelCell.setBackgroundColor(shaded ? ROW_ALT : Color.WHITE);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        valueCell.setBorderColor(BORDER_GRAY);
        valueCell.setBackgroundColor(shaded ? ROW_ALT : Color.WHITE);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}