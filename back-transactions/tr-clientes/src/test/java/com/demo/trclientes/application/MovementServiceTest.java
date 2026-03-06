package com.demo.trclientes.application;

import com.demo.trclientes.application.movement.MovementService;
import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.account.ports.out.AccountRepositoryPort;
import com.demo.trclientes.domain.movement.Movement;
import com.demo.trclientes.domain.movement.MovementManagerService;
import com.demo.trclientes.domain.movement.MovementType;
import com.demo.trclientes.domain.movement.ports.out.MovementRepositoryPort;
import com.demo.trclientes.domain.shared.exceptions.ConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private MovementRepositoryPort movementRepository;

    @Mock
    private MovementManagerService movementManagerService;

    @InjectMocks
    private MovementService movementService;

    @Test
    @DisplayName("Create: Debería registrar un movimiento exitosamente")
    void create_ShouldProcessMovementSuccessfully() {
        // ARRANGE
        Long accountId = 1L;
        Account mockAccount = Account.builder().id(accountId).balance(new BigDecimal("100.00")).build();
        Movement request = Movement.builder().accountId(accountId).movementType(MovementType.DEPOSITO).value(new BigDecimal("50.00")).build();

        when(accountRepository.getActiveAccountsById(accountId)).thenReturn(mockAccount);
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        Movement response = movementService.create(request);

        // ASSERT
        assertNotNull(response);
        verify(movementManagerService).runMovement(request, mockAccount);
        verify(accountRepository).save(mockAccount);
        verify(movementRepository).save(request);
    }

    @Test
    @DisplayName("Update: Debería fallar si no es el último movimiento")
    void update_ShouldThrowConflictException_WhenIsNotLastMovement() {
        // ARRANGE
        Long movementId = 1L;
        Long lastMovementId = 2L;
        Long accountId = 10L;

        Movement original = Movement.builder().id(movementId).accountId(accountId).build();
        Movement last = Movement.builder().id(lastMovementId).accountId(accountId).build();
        Movement updateRequest = Movement.builder().value(new BigDecimal("20.00")).build();

        when(movementRepository.getMovementById(movementId)).thenReturn(original);
        when(movementRepository.findLastByAccountId(accountId)).thenReturn(Optional.of(last));

        // ACT & ASSERT
        assertThrows(ConflictException.class, () -> movementService.update(movementId, updateRequest));
        verify(movementManagerService, never()).runMovementUpdate(any(), any(), any());
    }

    @Test
    @DisplayName("Delete: Debería ejecutar el reverso del movimiento")
    void delete_ShouldExecuteReverseMovement() {
        // ARRANGE
        Long movementId = 1L;
        Long accountId = 10L;
        Account mockAccount = Account.builder().id(accountId).balance(new BigDecimal("150.00")).build();
        Movement original = Movement.builder().id(movementId).accountId(accountId).movementType(MovementType.DEPOSITO).value(new BigDecimal("50.00")).build();

        when(movementRepository.getMovementById(movementId)).thenReturn(original);
        when(accountRepository.getActiveAccountsById(accountId)).thenReturn(mockAccount);

        // ACT
        movementService.delete(movementId);

        // ASSERT
        verify(movementManagerService).runReverse(eq(original), any(Movement.class), eq(mockAccount));
        verify(accountRepository).save(mockAccount);
        verify(movementRepository, times(2)).save(any(Movement.class));
    }
}
