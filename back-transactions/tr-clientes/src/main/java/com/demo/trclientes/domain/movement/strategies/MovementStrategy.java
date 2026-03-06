package com.demo.trclientes.domain.movement.strategies;

import com.demo.trclientes.domain.movement.MovementType;

import java.math.BigDecimal;

public interface MovementStrategy {
    MovementType getTipoMovimiento();
    BigDecimal calcularNuevoSaldo(BigDecimal balance, BigDecimal value);
}
