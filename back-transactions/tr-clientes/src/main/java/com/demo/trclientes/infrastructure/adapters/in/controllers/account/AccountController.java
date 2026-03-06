package com.demo.trclientes.infrastructure.adapters.in.controllers.account;

import com.demo.trclientes.domain.account.ports.in.AccountCommandServicePort;
import com.demo.trclientes.domain.account.ports.in.AccountQueryServicePort;
import com.demo.trclientes.infrastructure.adapters.in.rest.api.AccountsApi;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.*;
import com.demo.trclientes.infrastructure.shared.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountCommandServicePort cuentaCommandService;
    private final AccountQueryServicePort cuentaQueryService;
    private final AccountMapper accountMapper;

    @Override
    public ResponseEntity<ApiResponseCuentaResponse> createAccount(CuentaRequest cuentaRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [POST /api/v1/accounts] - Solicitud de creación de cuenta.");
        var domainRequest = accountMapper.toDomain(cuentaRequest);

        var domainResponse = cuentaCommandService.create(domainRequest);

        var restResponse = accountMapper.toResponse(domainResponse);

        ApiResponseCuentaResponse response = new ApiResponseCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteAccount(Long id) {
        log.warn("INICIO PETICIÓN (OpenAPI): [DELETE /api/v1/accounts/{}] - Solicitud de ELIMINACIÓN.", id);
        cuentaCommandService.delete(id);

        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ApiResponseCuentaResponse> getAccountById(Long id) {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/accounts/{}] - Búsqueda por ID.", id);
        var domainResponse = cuentaQueryService.getById(id);
        var restResponse = accountMapper.toResponse(domainResponse);

        ApiResponseCuentaResponse response = new ApiResponseCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseListCuentaResponse> getAllAccounts() {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/accounts] - Listado completo.");
        List<CuentaResponse> restList = cuentaQueryService.getAll().stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());

        ApiResponseListCuentaResponse response = new ApiResponseListCuentaResponse();
        response.setStatus(true);
        response.setData(restList);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseCuentaResponse> updateAccount(Long id, CuentaRequest cuentaRequest) {
        log.info("INICIO PETICIÓN (OpenAPI): [PUT /api/v1/accounts/{}] - Actualización.", id);
        var domainRequest = accountMapper.toDomain(cuentaRequest);
        var domainResponse = cuentaCommandService.update(id, domainRequest);
        var restResponse = accountMapper.toResponse(domainResponse);

        ApiResponseCuentaResponse response = new ApiResponseCuentaResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }
}
