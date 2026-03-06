package com.demo.trclientes.domain.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AccountReport {
    private String accountNumber;
    private String accountType;
    private BigDecimal currentBalance;
    private Boolean status;
    private List<MovementReport> movements;
}
