package com.demo.trclientes.domain.movement.strategies;

import com.demo.trclientes.domain.movement.MovementType;
import com.demo.trclientes.domain.shared.exceptions.LowBalanceException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RetiroStrategy implements MovementStrategy {
    @Override
    public MovementType getTipoMovimiento() {
        return MovementType.RETIRO;
    }

    @Override
    public BigDecimal calcularNuevoSaldo(BigDecimal balance, BigDecimal valor) {
        BigDecimal newBalance = balance.subtract(valor);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new LowBalanceException("Saldo no disponible");
        }
        return newBalance;
    }
}

