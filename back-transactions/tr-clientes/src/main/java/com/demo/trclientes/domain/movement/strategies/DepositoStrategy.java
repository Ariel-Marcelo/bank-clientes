package com.demo.trclientes.domain.movement.strategies;

import com.demo.trclientes.domain.movement.MovementType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositoStrategy implements MovementStrategy {
    @Override
    public MovementType getTipoMovimiento() {
        return MovementType.DEPOSITO;
    }

    @Override
    public BigDecimal calcularNuevoSaldo(BigDecimal balance, BigDecimal valor) {
        return balance.add(valor);
    }
}

