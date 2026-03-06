package com.demo.trclientes.infrastructure.adapters.in.controllers.movement;


import com.demo.trclientes.domain.movement.Movement;
import com.demo.trclientes.domain.movement.ports.in.MovementCommandServicePort;
import com.demo.trclientes.domain.movement.ports.in.MovementQueryServicePort;
import com.demo.trclientes.infrastructure.adapters.in.rest.api.MovementsApi;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.*;
import com.demo.trclientes.infrastructure.shared.mappers.MovementMapper;
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
public class MovementController implements MovementsApi {

    private final MovementCommandServicePort movementCommandService;
    private final MovementQueryServicePort movementQueryService;
    private final MovementMapper movementMapper;

    @Override
    public ResponseEntity<ApiResponseMovimientoResponse> createMovement(MovimientoRequest request) {
        log.info("INICIO PETICIÓN (OpenAPI): [POST /api/v1/movements] para cuenta: {}", request.getNumeroCuenta());
        
        Movement domain = movementMapper.toDomain(request);

        var domainResponse = movementCommandService.create(domain);

        var restResponse = movementMapper.toResponse(domainResponse);

        ApiResponseMovimientoResponse response = new ApiResponseMovimientoResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ApiResponseVoid> deleteMovement(Long id) {
        log.warn("INICIO PETICIÓN (OpenAPI): [DELETE /api/v1/movements/{}]", id);
        movementCommandService.delete(id);

        ApiResponseVoid response = new ApiResponseVoid();
        response.setStatus(true);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ApiResponseListMovimientoResponse> getAllMovements() {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/movements]");
        List<MovimientoResponse> restList = movementQueryService.getAll().stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());

        ApiResponseListMovimientoResponse response = new ApiResponseListMovimientoResponse();
        response.setStatus(true);
        response.setData(restList);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseMovimientoResponse> getMovementById(Long id) {
        log.info("INICIO PETICIÓN (OpenAPI): [GET /api/v1/movements/{}]", id);
        var domainResponse = movementQueryService.getById(id);
        var restResponse = movementMapper.toResponse(domainResponse);

        ApiResponseMovimientoResponse response = new ApiResponseMovimientoResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponseMovimientoResponse> updateMovement(Long id, MovimientoRequest request) {
        log.info("INICIO PETICIÓN (OpenAPI): [PUT /api/v1/movements/{}]", id);
        
        Movement domain = movementMapper.toDomain(request);

        var domainResponse = movementCommandService.update(id, domain);
        var restResponse = movementMapper.toResponse(domainResponse);

        ApiResponseMovimientoResponse response = new ApiResponseMovimientoResponse();
        response.setStatus(true);
        response.setData(restResponse);

        return ResponseEntity.ok(response);
    }
}
