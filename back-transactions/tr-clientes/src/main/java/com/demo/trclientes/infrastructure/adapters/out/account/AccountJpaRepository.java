package com.demo.trclientes.infrastructure.adapters.out.account;

import com.demo.trclientes.infrastructure.persistence.models.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findByStateTrue();

    Optional<AccountEntity> findByIdAndStateTrue(Long id);

    Optional<AccountEntity> findByNumberAndStateTrue(String number);

    List<AccountEntity> findByClient_ClientId(String clientId);

}
