package com.demo.trclientes.domain.account;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Account {
    private Long id;
    private String numberAccount;
    private AccountType accountType;
    private BigDecimal balance;
    private Boolean state;
    private Long clientId;
    private String clientName;

    public void inactivate() {
        this.state = false;
    }

    public void updateAccount ( Account updatedAccount) {
        this.clientId = updatedAccount.getClientId();
        this.state = updatedAccount.getState();
        this.accountType = updatedAccount.getAccountType();
    }

    public void initAccount() {
        this.state = true;
        this.balance = BigDecimal.valueOf(0L);
    }

    public void updateAmount(BigDecimal nuevoSaldo) {
        this.balance = nuevoSaldo;
    }

    public BigDecimal substractAmount(BigDecimal value) {
        return this.balance.subtract(value);
    }

}
