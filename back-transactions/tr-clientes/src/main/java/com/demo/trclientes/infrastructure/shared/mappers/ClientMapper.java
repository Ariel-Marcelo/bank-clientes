package com.demo.trclientes.infrastructure.shared.mappers;

import com.demo.trclientes.domain.client.models.Client;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.ClienteResponse;
import com.demo.trclientes.infrastructure.persistence.models.ClientEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "state", source = "active")
    ClientEntity toEntity(Client client);

    @Mapping(target = "active", source = "state")
    Client toDomain(ClientEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "gender", source = "genero")
    @Mapping(target = "age", source = "edad")
    @Mapping(target = "identification", source = "identificacion")
    @Mapping(target = "address", source = "direccion")
    @Mapping(target = "phone", source = "telefono")
    @Mapping(target = "password", source = "contrasenia")
    @Mapping(target = "active", source = "estado")
    @Mapping(target = "clientId", source = "clienteId")
    Client toDomain(ClienteRequest request);

    @Mapping(target = "nombre", source = "name")
    @Mapping(target = "genero", source = "gender")
    @Mapping(target = "edad", source = "age")
    @Mapping(target = "identificacion", source = "identification")
    @Mapping(target = "direccion", source = "address")
    @Mapping(target = "telefono", source = "phone")
    @Mapping(target = "estado", source = "active")
    @Mapping(target = "clienteId", source = "clientId")
    ClienteResponse toResponse(Client client);
}
