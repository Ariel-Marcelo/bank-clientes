package com.demo.trclientes.domain.movement.ports.in;

import com.demo.trclientes.domain.movement.Movement;

import java.util.List;

public interface MovementQueryServicePort {
    List<Movement> getAll();
    Movement getById(Long id);
}
