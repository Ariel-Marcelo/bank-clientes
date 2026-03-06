package com.demo.trclientes.domain.movement.strategies;

import com.demo.trclientes.domain.movement.MovementType;
import com.demo.trclientes.domain.shared.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MovementStrategyFactory {

    private final Map<MovementType, MovementStrategy> strategies;

    public MovementStrategyFactory(List<MovementStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        MovementStrategy::getTipoMovimiento,
                        Function.identity()
                ));
    }

    public MovementStrategy getStrategy(MovementType tipo) {
        MovementStrategy strategy = strategies.get(tipo);
        if (strategy == null) {
            throw new BadRequestException("Tipo de movimiento no soportado: " + tipo);
        }
        return strategy;
    }
}
