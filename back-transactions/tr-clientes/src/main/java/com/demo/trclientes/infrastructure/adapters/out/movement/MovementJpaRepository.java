package com.demo.trclientes.infrastructure.adapters.out.movement;

import com.demo.trclientes.infrastructure.persistence.models.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementJpaRepository extends JpaRepository<MovementEntity, Long> {
    Optional<MovementEntity> findTopByAccount_IdOrderByIdDesc(Long accountId);

    @Query("SELECT m FROM MovementEntity m " +
           "JOIN FETCH m.account c " +
           "JOIN FETCH c.client cl " +
           "WHERE cl.clientId = :clienteId " +
           "AND m.date BETWEEN :inicio AND :fin " +
           "ORDER BY m.date DESC")
    List<MovementEntity> findReportByClienteAndDates(
        @Param("clienteId") String clienteId, 
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin);
}
