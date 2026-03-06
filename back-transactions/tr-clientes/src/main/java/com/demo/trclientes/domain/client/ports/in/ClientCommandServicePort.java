package com.demo.trclientes.domain.client.ports.in;

import com.demo.trclientes.domain.client.models.Client;

public interface ClientCommandServicePort {
    Client create(Client client);
    Client update(Long id, Client client);
    void delete(Long id);
}
