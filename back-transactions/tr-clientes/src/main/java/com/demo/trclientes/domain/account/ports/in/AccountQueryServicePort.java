package com.demo.trclientes.domain.account.ports.in;

import com.demo.trclientes.domain.account.Account;

import java.util.List;

public interface AccountQueryServicePort {
    List<Account> getAll();
    Account getById(Long id);
}
