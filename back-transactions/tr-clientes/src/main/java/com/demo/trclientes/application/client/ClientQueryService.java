package com.demo.trclientes.application.client;

import com.demo.trclientes.domain.client.models.Client;
import com.demo.trclientes.domain.client.ports.in.ClientQueryServicePort;
import com.demo.trclientes.domain.client.ports.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientQueryService implements ClientQueryServicePort {

    private final ClientRepositoryPort repository;

    @Override
    public List<Client> getAll() {
        log.info("INICIO GET ALL CLIENTES: Recuperando todos los clientes activos.");

        List<Client> clients = repository.getAllActiveClients();

        log.info("FIN GET ALL CLIENTES: {} clientes activos recuperados.", clients.size());
        return clients;
    }

    @Override
    public Client getById(Long id) {
        log.info("INICIO GET BY ID CLIENTE: Buscando cliente activo por ID {}.", id);
        
        Client client = repository.getActiveClientById(id);
        
        log.info("FIN GET BY ID CLIENTE: Cliente ID {} encontrado.", id);
        return client;
    }
}
