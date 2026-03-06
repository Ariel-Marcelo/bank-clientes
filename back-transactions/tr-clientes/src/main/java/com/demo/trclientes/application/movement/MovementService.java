package com.demo.trclientes.application.movement;

import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.account.ports.out.AccountRepositoryPort;
import com.demo.trclientes.domain.movement.Movement;
import com.demo.trclientes.domain.movement.MovementManagerService;
import com.demo.trclientes.domain.movement.ports.in.MovementCommandServicePort;
import com.demo.trclientes.domain.movement.ports.in.MovementQueryServicePort;
import com.demo.trclientes.domain.movement.ports.out.MovementRepositoryPort;

import com.demo.trclientes.domain.shared.exceptions.ConflictException;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService implements MovementCommandServicePort, MovementQueryServicePort {

    private final AccountRepositoryPort accountRepository;
    private final MovementRepositoryPort movementRepository;
    private final MovementManagerService movementManager;

    @Override
    @Transactional
    public Movement create(Movement movement) {
        log.info("INICIO TX CREATE: Procesando {} de {} en account {}.",
                movement.getMovementType(), movement.getValue(), 
                movement.getAccountNumber() != null ? movement.getAccountNumber() : movement.getAccountId());

        Account account = (movement.getAccountId() != null)
                ? accountRepository.getActiveAccountsById(movement.getAccountId())
                : accountRepository.findActiveAccountsByNumberId(movement.getAccountNumber());

        movementManager.runMovement(movement, account);

        accountRepository.save(account);
        return movementRepository.save(movement);
    }

    @Override
    public List<Movement> getAll() {
        return movementRepository.getAllMovements();
    }

    @Override
    public Movement getById(Long id) {
        return movementRepository.getMovementById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("INICIO TX REVERSO: Movimiento ID: {}", id);

        Movement original = movementRepository.getMovementById(id);
        Account account = accountRepository.getActiveAccountsById(original.getAccountId());

        Movement reverse = original.createReverse(account.getBalance());
        
        movementManager.runReverse(original, reverse, account);

        accountRepository.save(account);
        movementRepository.save(original);
        movementRepository.save(reverse);
    }

    @Override
    @Transactional
    public Movement update(Long id, Movement nuevosDatos) {
        log.warn("INICIO TX UPDATE: Movimiento ID: {}", id);

        Movement original = movementRepository.getMovementById(id);
        Movement ultimo = movementRepository.findLastByAccountId(original.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Historial de movimientos", "Cuenta ID", original.getAccountId()));
        Account account = accountRepository.getActiveAccountsById(original.getAccountId());

        if (!original.getId().equals(ultimo.getId())) {
            throw new ConflictException("Solo se permite editar el último movimiento para mantener la integridad del saldo.");
        }

        movementManager.runMovementUpdate(original, nuevosDatos, account);

        accountRepository.save(account);
        return movementRepository.save(original);
    }
}
