package com.demo.trclientes.infrastructure.persistence.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre")
    private String name;

    @Column(name = "genero")
    private String gender;

    @Column(name = "edad")
    private int age;

    @Column(name="identificacion", unique = true)
    private String identification;

    @Column(name = "direccion")
    private String address;

    @Column(name = "telefono")
    private String phone;
}
