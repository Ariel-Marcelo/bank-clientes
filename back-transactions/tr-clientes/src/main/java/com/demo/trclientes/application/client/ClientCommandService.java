package com.demo.trclientes.application.client;

import com.demo.trclientes.domain.client.models.Client;
import com.demo.trclientes.domain.client.ports.in.ClientCommandServicePort;
import com.demo.trclientes.domain.client.ports.out.ClientRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientCommandService implements ClientCommandServicePort {

    private final ClientRepositoryPort repository;

    @Override
    public Client create(Client clientDomain) {
        log.info("INICIO CREATE CLIENTE: Creando cliente con Identificación: {}", clientDomain.getIdentification());
        Client savedClient = repository.save(clientDomain);

        log.info("FIN CREATE CLIENTE: Cliente ID {} creado y replicación exitosa. Transacción completada.", savedClient.getId());
        return savedClient;
    }

    @Override
    public Client update(Long id, Client clientDomain) {
        log.warn("INICIO UPDATE CLIENTE: Actualizando Cliente ID {}. Identificación: {}", id, clientDomain.getIdentification());

        Client existingClient = repository.getActiveClientById(id);
        
        // Actualizar campos permitidos
        existingClient.setName(clientDomain.getName() != null ? clientDomain.getName().trim() : existingClient.getName());
        existingClient.setGender(clientDomain.getGender() != null ? clientDomain.getGender() : existingClient.getGender());
        existingClient.setAge(clientDomain.getAge() != 0 ? clientDomain.getAge() : existingClient.getAge());
        existingClient.setIdentification(clientDomain.getIdentification() != null ? clientDomain.getIdentification() : existingClient.getIdentification());
        existingClient.setAddress(clientDomain.getAddress() != null ? clientDomain.getAddress() : existingClient.getAddress());
        existingClient.setPhone(clientDomain.getPhone() != null ? clientDomain.getPhone() : existingClient.getPhone());
        existingClient.setClientId(clientDomain.getClientId() != null ? clientDomain.getClientId() : existingClient.getClientId());
        existingClient.setActive(clientDomain.isActive());

        // Seguridad: No sobreescribir contraseña si viene vacía
        if (clientDomain.getPassword() != null && !clientDomain.getPassword().isBlank()) {
            existingClient.setPassword(clientDomain.getPassword());
        }

        Client updatedClient = repository.save(existingClient);

        log.warn("FIN UPDATE CLIENTE: Cliente ID {} actualizado. Transacción completada.", id);
        return updatedClient;
    }

    @Override
    public void delete(Long id) {
        log.error("INICIO DELETE CLIENTE: Solicitud de ELIMINACIÓN LÓGICA para Cliente ID {}.", id);

        Client client = repository.getActiveClientById(id);
        client.inactivate();

        repository.save(client);
        log.info("CLIENTE INHABILITADO: Cliente ID {} marcado como INACTIVO en BD local.", id);
    }
}
