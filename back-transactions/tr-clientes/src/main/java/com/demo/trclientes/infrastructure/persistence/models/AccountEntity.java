package com.demo.trclientes.infrastructure.persistence.models;

import com.demo.trclientes.domain.account.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "cuentas")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="numeroCuenta", unique = true, nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name="tipoCuenta")
    private AccountType typeAccount;

    @NotNull
    @Column(name="saldoInicial")
    private BigDecimal salary;

    @Column(name="estado")
    private boolean state;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovementEntity> movements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientId")
    private ClientEntity client;
}
