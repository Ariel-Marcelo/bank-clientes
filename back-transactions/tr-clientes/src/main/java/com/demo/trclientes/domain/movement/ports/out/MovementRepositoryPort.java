package com.demo.trclientes.domain.movement.ports.out;

import com.demo.trclientes.domain.movement.Movement;

import java.util.List;
import java.util.Optional;

public interface MovementRepositoryPort {
    Movement save(Movement movement);
    List<Movement> getAllMovements();
    Movement getMovementById(Long id);
    Optional<Movement> findLastByAccountId(Long accountId);
}
