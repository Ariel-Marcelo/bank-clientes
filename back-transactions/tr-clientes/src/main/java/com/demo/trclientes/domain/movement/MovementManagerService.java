package com.demo.trclientes.domain.movement;

import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.movement.strategies.MovementStrategy;
import com.demo.trclientes.domain.movement.strategies.MovementStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementManagerService {

    private final MovementStrategyFactory strategyFactory;

    public void runMovement(Movement movement, Account cuenta) {
        log.info("DOMINIO: Ejecutando movimiento de tipo {} para cuenta {}", movement.getMovementType(), cuenta.getNumberAccount());
        
        MovementStrategy strategy = strategyFactory.getStrategy(movement.getMovementType());
        BigDecimal newBalance = strategy.calcularNuevoSaldo(cuenta.getBalance(), movement.getValue().abs());

        movement.createMovement(cuenta, newBalance);
        cuenta.updateAmount(newBalance);
    }

    public void runReverse(Movement original, Movement reverso, Account cuenta) {
        log.info("DOMINIO: Ejecutando reverso del movimiento ID {} para cuenta {}", original.getId(), cuenta.getNumberAccount());

        original.tagReverseMovement();
        
        MovementStrategy strategy = strategyFactory.getStrategy(reverso.getMovementType());
        BigDecimal finalBalance = strategy.calcularNuevoSaldo(cuenta.getBalance(), reverso.getValue().abs());

        reverso.createMovement(cuenta, finalBalance);
        cuenta.updateAmount(finalBalance);
    }

    public void runMovementUpdate(Movement original, Movement nuevosDatos, Account cuenta) {
        log.info("DOMINIO: Ejecutando actualización del movimiento ID {} para cuenta {}", original.getId(), cuenta.getNumberAccount());

        BigDecimal newAmount = cuenta.substractAmount(original.getValue());
        
        MovementStrategy strategy = strategyFactory.getStrategy(nuevosDatos.getMovementType());
        BigDecimal newBalance = strategy.calcularNuevoSaldo(newAmount, nuevosDatos.getValue().abs());

        BigDecimal value = (nuevosDatos.getMovementType() == MovementType.RETIRO)
                ? nuevosDatos.getValue().abs().negate()
                : nuevosDatos.getValue().abs();

        original.updateAmount(value, newBalance);
        cuenta.updateAmount(newBalance);
    }
}
