package com.demo.trclientes.infrastructure.adapters.out.client;

import com.demo.trclientes.domain.client.ports.out.ClientRepositoryPort;
import com.demo.trclientes.domain.client.models.Client;
import com.demo.trclientes.infrastructure.shared.mappers.ClientMapper;
import com.demo.trclientes.infrastructure.persistence.models.ClientEntity;
import com.demo.trclientes.domain.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {

    private final ClientJpaRepository jpaRepository;
    private final ClientMapper mapper;

    @Override
    public Client save(Client clientDomain) {
        // Validación de unicidad
        if (clientDomain.getId() == null) {
            // Creación
            if (jpaRepository.existsByIdentification(clientDomain.getIdentification())) {
                throw new com.demo.trclientes.domain.shared.exceptions.ConflictException(
                    "Ya existe un cliente con la identificación: " + clientDomain.getIdentification());
            }
            if (jpaRepository.existsByClientId(clientDomain.getClientId())) {
                throw new com.demo.trclientes.domain.shared.exceptions.ConflictException(
                    "Ya existe un cliente con el Client ID: " + clientDomain.getClientId());
            }
        } else {
            // Actualización: verificar que la identificación/clientId no pertenezcan a OTRO registro
            jpaRepository.findByIdentification(clientDomain.getIdentification())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(clientDomain.getId())) {
                        throw new com.demo.trclientes.domain.shared.exceptions.ConflictException(
                            "La identificación ya está en uso por otro cliente");
                    }
                });

            jpaRepository.findByClientId(clientDomain.getClientId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(clientDomain.getId())) {
                        throw new com.demo.trclientes.domain.shared.exceptions.ConflictException(
                            "El Client ID ya está en uso por otro cliente");
                    }
                });
        }

        ClientEntity entity = mapper.toEntity(clientDomain);
        ClientEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Client> getAllActiveClients() {
        return jpaRepository.findByStateTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Client getActiveClientById(Long id) {
        ClientEntity entity = jpaRepository.findByIdAndStateTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado o inactivo con ID: " + id));
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Client> findByStateAndActive(Long id) {
        return jpaRepository.findByIdAndStateTrue(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
