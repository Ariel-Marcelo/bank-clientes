package com.demo.trclientes.infrastructure.adapters.out.movement;

import com.demo.trclientes.domain.movement.Movement;
import com.demo.trclientes.infrastructure.shared.mappers.MovementMapper;
import com.demo.trclientes.domain.movement.ports.out.MovementRepositoryPort;
import com.demo.trclientes.infrastructure.adapters.out.account.AccountJpaRepository;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import com.demo.trclientes.infrastructure.persistence.models.AccountEntity;
import com.demo.trclientes.infrastructure.persistence.models.MovementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MovementRepository implements MovementRepositoryPort {

    private final MovementJpaRepository jpaRepository;
    private final AccountJpaRepository accountRepository;
    private final MovementMapper movementMapper;

    @Override
    public Movement save(Movement movement) {
        MovementEntity entity = movementMapper.toEntity(movement);
        
        if (movement.getAccountId() != null) {
            AccountEntity account = accountRepository.findById(movement.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "ID", movement.getAccountId()));
            entity.setAccount(account);
        }
        
        return movementMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Movement> getAllMovements() {
        return jpaRepository.findAll().stream()
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Movement getMovementById(Long id) {
        return jpaRepository.findById(id)
                .map(movementMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "ID", id));
    }

    @Override
    public Optional<Movement> findLastByAccountId(Long accountId) {
        return jpaRepository.findTopByAccount_IdOrderByIdDesc(accountId)
                .map(movementMapper::toDomain);
    }
}
