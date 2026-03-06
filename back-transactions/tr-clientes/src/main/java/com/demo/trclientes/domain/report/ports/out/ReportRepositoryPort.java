package com.demo.trclientes.domain.report.ports.out;


import com.demo.trclientes.domain.report.AccountStatementReport;
import com.demo.trclientes.domain.report.ReportQuery;

import java.util.Optional;

public interface ReportRepositoryPort {
    Optional<AccountStatementReport> getStateAccount(ReportQuery query);
}
