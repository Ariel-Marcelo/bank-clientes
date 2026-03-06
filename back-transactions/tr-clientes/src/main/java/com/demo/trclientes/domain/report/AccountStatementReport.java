package com.demo.trclientes.domain.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountStatementReport {
    private String clientId;
    private String clientName;
    private String requestedDateRange;
    private String pdfContent;
    private List<AccountReport> accounts;
}
