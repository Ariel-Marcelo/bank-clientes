package com.demo.trclientes.application.account;

import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.account.ports.in.AccountCommandServicePort;
import com.demo.trclientes.domain.account.ports.in.AccountQueryServicePort;
import com.demo.trclientes.domain.account.ports.out.AccountRepositoryPort;
import com.demo.trclientes.domain.client.ports.out.ClientRepositoryPort;
import com.demo.trclientes.domain.shared.exceptions.BadRequestException;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements AccountCommandServicePort, AccountQueryServicePort {

    private final AccountRepositoryPort accountRepository;
    private final ClientRepositoryPort clientRepository;

    @Override
    @Transactional
    public Account create(Account account) {
        log.info("INICIO CREATE CUENTA: Solicitud para cuenta N° {} de Cliente ID {}.", account.getNumberAccount(), account.getClientId());

        boolean exist = clientRepository.existsById(account.getClientId());

        if (!exist) {
            throw new ResourceNotFoundException("Cliente", "ID", account.getClientId());
        }
        account.initAccount();

        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAll() {
        return accountRepository.getAllActiveAccounts();
    }

    @Override
    public Account getById(Long id) {
        return accountRepository.getActiveAccountsById(id);
    }

    @Override
    @Transactional
    public Account update(Long id, Account account) {
        log.warn("INICIO UPDATE CUENTA: Solicitud para ID {}.", id);

        clientRepository.findByStateAndActive(account.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "ID", account.getClientId()));

        Account accountSaved = accountRepository.getActiveAccountsById(id);
        accountSaved.updateAccount(account);
        return accountRepository.save(accountSaved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Account account = accountRepository.getActiveAccountsById(id);
        account.inactivate();
        accountRepository.save(account);
    }
}
