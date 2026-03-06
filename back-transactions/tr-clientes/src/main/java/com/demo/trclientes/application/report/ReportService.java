package com.demo.trclientes.application.report;

import com.demo.trclientes.domain.report.AccountStatementReport;
import com.demo.trclientes.domain.report.ReportQuery;
import com.demo.trclientes.domain.report.ports.out.ReportPdfPort;
import com.demo.trclientes.domain.report.ports.out.ReportRepositoryPort;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepositoryPort reporteRepository;
    private final ReportPdfPort reportPdfPort;

    @Transactional(readOnly = true)
    public AccountStatementReport generateReport(ReportQuery query) {
        log.info("INICIO REPORTE: Solicitando reporte para Cliente ID {} en rango: {} a {}", 
                query.getClientId(), query.getStartDate(), query.getEndDate());

        AccountStatementReport report = reporteRepository.getStateAccount(query)
                .orElseThrow(() -> {
                    log.warn("FALLO REPORTE: Cliente no encontrado con ID: {}", query.getClientId());
                    return new ResourceNotFoundException("Cliente", "ID", query.getClientId());
                });

        String pdfBase64 = reportPdfPort.generateAccountStatementBase64(report);
        report.setPdfContent(pdfBase64);

        return report;
    }
}
