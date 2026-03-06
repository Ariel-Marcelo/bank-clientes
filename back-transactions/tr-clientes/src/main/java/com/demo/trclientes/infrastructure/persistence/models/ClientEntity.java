package com.demo.trclientes.infrastructure.persistence.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
public class ClientEntity extends Person {

    @Column(name = "cliente_id", unique = true, nullable = false)
    private String clientId;

    @NotBlank
    @Column(name = "contrasenia")
    private String password;

    @Column(name = "estado")
    private boolean state;
}
