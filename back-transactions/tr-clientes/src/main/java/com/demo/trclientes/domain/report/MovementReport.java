package com.demo.trclientes.domain.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovementReport {
    private Long id;
    private LocalDateTime date;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
}
