package com.demo.trclientes.infrastructure.persistence.models;

import com.demo.trclientes.domain.movement.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "movimientos")
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fecha")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name="tipoMovimiento")
    private MovementType movementType;

    @NotNull
    @Column(name="valor")
    private BigDecimal value;

    @NotNull
    @Column(name="saldo")
    private BigDecimal balance;

    @ManyToOne
    private AccountEntity account;
}
