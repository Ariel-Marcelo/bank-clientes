package com.demo.trclientes.domain.client.ports.in;

import com.demo.trclientes.domain.client.models.Client;
import java.util.List;

public interface ClientQueryServicePort {
    List<Client> getAll();
    Client getById(Long id);
}
