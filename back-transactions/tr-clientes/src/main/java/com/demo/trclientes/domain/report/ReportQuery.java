package com.demo.trclientes.domain.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class ReportQuery {
    private String clientId;
    private LocalDate startDate;
    private LocalDate endDate;

    public LocalDateTime getActualStart() { return startDate.atStartOfDay(); }
    public LocalDateTime getActualEnd() { return endDate.atTime(LocalTime.MAX); }
}
