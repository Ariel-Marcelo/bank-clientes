package com.demo.trclientes.infrastructure.adapters.in.controllers.report;


import com.demo.trclientes.application.report.ReportService;
import com.demo.trclientes.domain.shared.exceptions.BadRequestException;
import com.demo.trclientes.infrastructure.adapters.in.rest.api.ReportsApi;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ApiResponseReporteEstadoCuentaResponse;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ReporteRequest;
import com.demo.trclientes.infrastructure.shared.mappers.ReporteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController implements ReportsApi {

    private final ReportService reportService;
    private final ReporteMapper reportMapper;

    @Override
    public ResponseEntity<ApiResponseReporteEstadoCuentaResponse> getReport(LocalDate startDate, LocalDate endDate, ReporteRequest reporteRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [POST /api/v1/reportes] para cliente: {}", reporteRequest.getClienteId());
        
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        var query = reportMapper.toDomain(reporteRequest.getClienteId(), startDate, endDate);

        var report = reportService.generateReport(query);

        var restResponse = reportMapper.toResponse(report);

        ApiResponseReporteEstadoCuentaResponse response = new ApiResponseReporteEstadoCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }
}
