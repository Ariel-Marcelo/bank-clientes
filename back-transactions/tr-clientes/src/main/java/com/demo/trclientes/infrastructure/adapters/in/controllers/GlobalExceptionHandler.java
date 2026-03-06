package com.demo.trclientes.infrastructure.adapters.in.controllers;

import com.demo.trclientes.domain.shared.exceptions.*;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ApiResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseError> handleNotFound(ResourceNotFoundException ex) {
        log.warn("WARN (404 Not Found): Recurso no encontrado. Mensaje: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseError().status(false).message(ex.getMessage()));
    }

    @ExceptionHandler(LowBalanceException.class)
    public ResponseEntity<ApiResponseError> handleLowBalance(LowBalanceException ex) {
        log.warn("WARN (400 Bad Request): Error de negocio (Saldo insuficiente). Mensaje: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseError().status(false).message(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseError> handleBadRequest(BadRequestException ex) {
        log.warn("WARN (400 Bad Request): Solicitud incorrecta. Mensaje: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseError().status(false).message(ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponseError> handleConflict(ConflictException ex) {
        log.warn("WARN (409 Conflict): Conflicto de datos. Mensaje: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseError().status(false).message(ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseError> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        log.warn("WARN (409 Conflict): Violación de integridad de datos. Detalle: {}", ex.getMostSpecificCause().getMessage());
        String message = "Conflicto de integridad de datos: Probablemente ya existe un registro con la misma identificación o código.";
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseError().status(false).message(message));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseError> handleCustomValidation(ValidationException ex) {
        log.warn("WARN (400 Bad Request): Fallo de validación personalizada. Errores: {}", ex.getErrors());
        String detail = ex.getErrors() != null ? ex.getErrors().toString() : ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseError().status(false).message("Error de validación: " + detail));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("WARN (400 Bad Request): Fallo de validación de Spring. Errores: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseError().status(false).message("Error de validación: " + errors));
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ApiResponseError> handleTechnical(TechnicalException ex) {
        log.error("ERROR TÉCNICO (500 Internal Server Error): {}. Causa: {}", ex.getMessage(), 
                ex.getCause() != null ? ex.getCause().getMessage() : "N/A");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseError().status(false).message("Error técnico procesando la solicitud: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseError> handleGeneral(Exception ex) {
        log.error("FATAL ERROR (500 Internal Server Error): Error no controlado. Mensaje: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseError().status(false).message("Ocurrió un error interno del servidor."));
    }
}
