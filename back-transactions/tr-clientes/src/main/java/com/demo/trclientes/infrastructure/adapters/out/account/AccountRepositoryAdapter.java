package com.demo.trclientes.infrastructure.adapters.out.account;

import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.account.ports.out.AccountRepositoryPort;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import com.demo.trclientes.infrastructure.adapters.out.client.ClientJpaRepository;
import com.demo.trclientes.infrastructure.persistence.models.AccountEntity;
import com.demo.trclientes.infrastructure.persistence.models.ClientEntity;
import com.demo.trclientes.infrastructure.shared.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final ClientJpaRepository clientRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account save(Account domain) {
        AccountEntity entity = jpaRepository.findById(domain.getId() != null ? domain.getId() : -1L)
                .orElse(accountMapper.toEntity(domain));
        
        if (domain.getId() != null) {
            entity.setNumber(domain.getNumberAccount());
            entity.setTypeAccount(domain.getAccountType());
            entity.setSalary(domain.getBalance());
            entity.setState(domain.getState());
        }

        if (domain.getClientId() != null) {
            ClientEntity clientEntity = clientRepository.findById(domain.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "ID", domain.getClientId()));
            entity.setClient(clientEntity);
        }

        return accountMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Account> getAllActiveAccounts() {
        return jpaRepository.findByStateTrue().stream()
                .map(accountMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Account getActiveAccountsById(Long id) {
        Optional<AccountEntity> account = jpaRepository.findByIdAndStateTrue(id);
        if (account.isEmpty()) {
            throw new ResourceNotFoundException("Cuenta", "ID", id);
        }
        return accountMapper.toDomain(account.get());
    }

    @Override
    public Account findActiveAccountsByNumberId(String numberAccount) {
        return jpaRepository.findByNumberAndStateTrue(numberAccount)
                .map(accountMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", "número", numberAccount));
    }
}
