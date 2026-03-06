package com.demo.trclientes.domain.account.ports.out;

import com.demo.trclientes.domain.account.Account;

import java.util.List;

public interface AccountRepositoryPort {
    Account save(Account Account);
    List<Account> getAllActiveAccounts();
    Account getActiveAccountsById(Long id);
    Account findActiveAccountsByNumberId(String numberAccount);
}
