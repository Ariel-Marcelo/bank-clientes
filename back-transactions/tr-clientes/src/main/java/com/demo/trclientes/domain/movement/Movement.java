package com.demo.trclientes.domain.movement;

import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.shared.exceptions.ConflictException;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Movement {
    private Long id;
    private LocalDateTime date;
    private MovementType movementType;
    private BigDecimal value;
    private BigDecimal balance;
    private Long accountId;
    private String accountNumber;
    private String accountType;

    public void createMovement(Account account, BigDecimal newBalance) {
        this.date = LocalDateTime.now();
        this.accountId = account.getId();
        this.accountNumber = account.getNumberAccount();
        this.accountType = account.getAccountType() != null ? account.getAccountType().name() : null;
        this.balance = newBalance;

        if (MovementType.RETIRO.equals(this.movementType) && this.value.compareTo(BigDecimal.ZERO) > 0) {
            this.value = this.value.negate();
        }
    }

    public Movement createReverse(BigDecimal saldoActualCuenta) {
        MovementType movementType = (this.movementType == MovementType.RETIRO)
                ? MovementType.DEPOSITO
                : MovementType.RETIRO;

        BigDecimal reverseValue = this.value.negate();

        return Movement.builder()
                .movementType(movementType)
                .value(reverseValue)
                .date(LocalDateTime.now())
                .accountId(this.accountId)
                .build();
    }

    public void tagReverseMovement() {
        if (this.movementType == MovementType.REVERSADO) {
            throw new ConflictException("El movimiento ya ha sido reversado anteriormente.");
        }
        this.movementType = MovementType.REVERSADO;
    }

    public void updateAmount(BigDecimal newValue, BigDecimal newBalance) {
        this.date = LocalDateTime.now();
        this.value = newValue;
        this.balance = newBalance;
    }
}
