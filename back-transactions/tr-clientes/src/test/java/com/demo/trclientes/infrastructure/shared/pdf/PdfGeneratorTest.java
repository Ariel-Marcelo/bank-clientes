package com.demo.trclientes.infrastructure.shared.pdf;

import com.demo.trclientes.domain.report.AccountReport;
import com.demo.trclientes.domain.report.AccountStatementReport;
import com.demo.trclientes.domain.report.MovementReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfGeneratorTest {

    private final PdfGenerator pdfGenerator = new PdfGenerator();

    @Test
    void generateAccountStatementBase64_ShouldReturnNonEmptyString() {
        // Arrange
        MovementReport movement = MovementReport.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .movementType("DEPOSITO")
                .amount(new BigDecimal("100.00"))
                .balance(new BigDecimal("100.00"))
                .build();

        AccountReport account = AccountReport.builder()
                .accountNumber("123456")
                .accountType("AHORRO")
                .currentBalance(new BigDecimal("100.00"))
                .status(true)
                .movements(List.of(movement))
                .build();

        AccountStatementReport report = AccountStatementReport.builder()
                .clientId("CL001")
                .clientName("Juan Perez")
                .requestedDateRange("2023-01-01 to 2023-12-31")
                .accounts(List.of(account))
                .build();

        // Act
        String result = pdfGenerator.generateAccountStatementBase64(report);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Base64 of PDF usually starts with JVBERi0 (for %PDF-)
        assertTrue(result.startsWith("JVBERi0"));
    }
}
