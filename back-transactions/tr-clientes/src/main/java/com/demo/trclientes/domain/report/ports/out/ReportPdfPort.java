package com.demo.trclientes.domain.report.ports.out;

import com.demo.trclientes.domain.report.AccountStatementReport;

public interface ReportPdfPort {
    String generateAccountStatementBase64(AccountStatementReport report);
}
