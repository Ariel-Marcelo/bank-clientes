package com.demo.trclientes.domain.client.ports.out;

import com.demo.trclientes.domain.client.models.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepositoryPort {

    Client save(Client client);

    List<Client> getAllActiveClients();

    Client getActiveClientById(Long id);


    Optional<Client> findByStateAndActive(Long id);

    boolean existsById(Long id);
}
