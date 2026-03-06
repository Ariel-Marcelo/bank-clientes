package com.demo.trclientes.infrastructure.shared.mappers;

import com.demo.trclientes.domain.report.AccountReport;
import com.demo.trclientes.domain.report.AccountStatementReport;
import com.demo.trclientes.domain.report.MovementReport;
import com.demo.trclientes.domain.report.ReportQuery;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ReporteEstadoCuentaResponse;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ReporteCuenta;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ReporteMovimiento;
import com.demo.trclientes.infrastructure.persistence.models.AccountEntity;
import com.demo.trclientes.infrastructure.persistence.models.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ReporteMapper {

    ReporteMapper INSTANCE = Mappers.getMapper(ReporteMapper.class);

    default ReportQuery toDomain(String clientId, LocalDate startDate, LocalDate endDate) {
        return ReportQuery.builder()
                .clientId(clientId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "movementType", expression = "java(movement.getMovementType() != null ? movement.getMovementType().name() : null)")
    @Mapping(target = "amount", source = "value")
    @Mapping(target = "balance", source = "balance")
    MovementReport toMovementDomain(MovementEntity movement);

    @Mapping(target = "accountNumber", source = "number")
    @Mapping(target = "accountType", expression = "java(account.getTypeAccount() != null ? account.getTypeAccount().name() : null)")
    @Mapping(target = "currentBalance", source = "salary")
    @Mapping(target = "status", source = "state")
    @Mapping(target = "movements", ignore = true)
    AccountReport toAccountDomain(AccountEntity account);

    @Mapping(target = "fecha", source = "date")
    @Mapping(target = "tipoMovimiento", source = "movementType")
    @Mapping(target = "valor", source = "amount")
    @Mapping(target = "saldo", source = "balance")
    ReporteMovimiento toReporteMovimientoDto(MovementReport domain);

    @Mapping(target = "numeroCuenta", source = "accountNumber")
    @Mapping(target = "tipoCuenta", source = "accountType")
    @Mapping(target = "saldoActual", source = "currentBalance")
    @Mapping(target = "estado", source = "status")
    @Mapping(target = "movimientos", source = "movements")
    ReporteCuenta toReporteCuentaDto(AccountReport domain);

    @Mapping(target = "clienteId", source = "clientId")
    @Mapping(target = "nombreCliente", source = "clientName")
    @Mapping(target = "rangoFechasSolicitado", source = "requestedDateRange")
    @Mapping(target = "pdfContent", source = "pdfContent")
    @Mapping(target = "cuentas", source = "accounts")
    ReporteEstadoCuentaResponse toResponse(AccountStatementReport domain);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}
