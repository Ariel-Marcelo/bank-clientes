package com.demo.trclientes.domain.movement.ports.in;

import com.demo.trclientes.domain.movement.Movement;

public interface MovementCommandServicePort {
    Movement create(Movement movement);
    Movement update(Long id, Movement movement);
    void delete(Long id);
}
