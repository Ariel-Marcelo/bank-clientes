package com.demo.trclientes.infrastructure.adapters.out.client;

import com.demo.trclientes.infrastructure.persistence.models.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, Long> {
    List<ClientEntity> findByStateTrue();

    Optional<ClientEntity> findByIdAndStateTrue(Long id);

    Optional<ClientEntity> findByClientId(String clientId);

    boolean existsByIdentification(String identification);

    boolean existsByClientId(String clientId);

    Optional<ClientEntity> findByIdentification(String identification);

}
