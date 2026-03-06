package com.demo.trclientes.infrastructure.shared.pdf;

import com.demo.trclientes.domain.report.AccountReport;
import com.demo.trclientes.domain.report.AccountStatementReport;
import com.demo.trclientes.domain.report.MovementReport;
import com.demo.trclientes.domain.report.ports.out.ReportPdfPort;
import com.demo.trclientes.domain.shared.exceptions.TechnicalException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
public class PdfGenerator implements ReportPdfPort {

    @Override
    public String generateAccountStatementBase64(AccountStatementReport report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();
            addTitle(document, "Estado de Cuenta");
            addClientInfo(document, report);
            
            for (AccountReport account : report.getAccounts()) {
                addAccountInfo(document, account);
                addMovementsTable(document, account);
            }

            document.close();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new TechnicalException("Error al generar el PDF del reporte", e);
        }
    }

    private void addTitle(Document document, String titleText) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph para = new Paragraph(titleText, font);
        para.setAlignment(Paragraph.ALIGN_CENTER);
        para.setSpacingAfter(20);
        document.add(para);
    }

    private void addClientInfo(Document document, AccountStatementReport report) throws DocumentException {
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);

        Paragraph p1 = new Paragraph();
        p1.add(new Phrase("Cliente: ", fontBold));
        p1.add(new Phrase(report.getClientName(), fontNormal));
        document.add(p1);

        Paragraph p2 = new Paragraph();
        p2.add(new Phrase("ID Cliente: ", fontBold));
        p2.add(new Phrase(report.getClientId(), fontNormal));
        document.add(p2);

        Paragraph p3 = new Paragraph();
        p3.add(new Phrase("Rango de Fechas: ", fontBold));
        p3.add(new Phrase(report.getRequestedDateRange(), fontNormal));
        p3.add(new Paragraph("\n"));
        document.add(p3);
    }

    private void addAccountInfo(Document document, AccountReport account) throws DocumentException {
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Paragraph p = new Paragraph();
        p.add(new Phrase("Cuenta: ", fontBold));
        p.add(new Phrase(account.getAccountNumber() + " (" + account.getAccountType() + ")", fontNormal));
        p.add(new Phrase(" - Saldo Actual: ", fontBold));
        p.add(new Phrase("$" + account.getCurrentBalance().toString(), fontNormal));
        p.setSpacingBefore(10);
        p.setSpacingAfter(5);
        document.add(p);
    }

    private void addMovementsTable(Document document, AccountReport account) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(15);

        addTableHeader(table);

        for (MovementReport movement : account.getMovements()) {
            table.addCell(movement.getDate().toString());
            table.addCell(movement.getMovementType());
            table.addCell(movement.getAmount().toString());
            table.addCell(movement.getBalance().toString());
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setBorderWidth(1);
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        header.setPhrase(new Phrase("Fecha", font));
        table.addCell(header);

        header.setPhrase(new Phrase("Tipo", font));
        table.addCell(header);

        header.setPhrase(new Phrase("Valor", font));
        table.addCell(header);

        header.setPhrase(new Phrase("Saldo", font));
        table.addCell(header);
    }
}
