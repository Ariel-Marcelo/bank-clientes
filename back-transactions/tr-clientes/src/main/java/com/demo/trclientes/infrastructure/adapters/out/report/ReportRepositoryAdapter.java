package com.demo.trclientes.infrastructure.adapters.out.report;

import com.demo.trclientes.domain.report.*;
import com.demo.trclientes.domain.report.ports.out.ReportRepositoryPort;

import com.demo.trclientes.infrastructure.adapters.out.account.AccountJpaRepository;
import com.demo.trclientes.infrastructure.adapters.out.client.ClientJpaRepository;
import com.demo.trclientes.infrastructure.adapters.out.movement.MovementJpaRepository;
import com.demo.trclientes.infrastructure.shared.mappers.ReporteMapper;
import com.demo.trclientes.infrastructure.persistence.models.AccountEntity;
import com.demo.trclientes.infrastructure.persistence.models.ClientEntity;
import com.demo.trclientes.infrastructure.persistence.models.MovementEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepositoryPort {

    private final ClientJpaRepository clientRepository;
    private final AccountJpaRepository accountRepository;
    private final MovementJpaRepository movementRepository;
    private final ReporteMapper reportMapper;

    @Override
    public Optional<AccountStatementReport> getStateAccount(ReportQuery query) {
        String clientId = query.getClientId();

        Optional<ClientEntity> clientOpt = clientRepository.findByClientId(clientId);
        if (clientOpt.isEmpty()) {
            return Optional.empty();
        }
        ClientEntity client = clientOpt.get();
        List<AccountEntity> accounts = accountRepository.findByClient_ClientId(clientId);

        List<MovementEntity> allMovements = movementRepository.findReportByClienteAndDates(
            clientId, query.getActualStart(), query.getActualEnd());

        Map<Long, List<MovementEntity>> movementsByCuentaId = allMovements.stream()
                .collect(Collectors.groupingBy(m -> m.getAccount().getId()));

        List<AccountReport> accountsDomain = accounts.stream().map(cuenta -> {
            List<MovementReport> movementsDomain = movementsByCuentaId
                    .getOrDefault(cuenta.getId(), Collections.emptyList())
                    .stream()
                    .map(reportMapper::toMovementDomain)
                    .collect(Collectors.toList());

            AccountReport accountDomain = reportMapper.toAccountDomain(cuenta);
            accountDomain.setMovements(movementsDomain);
            return accountDomain;
        }).collect(Collectors.toList());

        return Optional.of(AccountStatementReport.builder()
                .clientId(client.getClientId())
                .clientName(client.getName())
                .requestedDateRange(query.getStartDate() + " to " + query.getEndDate())
                .accounts(accountsDomain)
                .build());
    }
}
