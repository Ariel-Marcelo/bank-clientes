package com.demo.trclientes.domain.account.ports.in;

import com.demo.trclientes.domain.account.Account;

public interface AccountCommandServicePort {
    Account create(Account domain);
    Account update(Long id, Account domain);
    void delete(Long id);
}
