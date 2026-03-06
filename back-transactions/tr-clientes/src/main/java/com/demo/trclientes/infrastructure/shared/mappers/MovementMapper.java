package com.demo.trclientes.infrastructure.shared.mappers;

import com.demo.trclientes.domain.movement.Movement;
import com.demo.trclientes.infrastructure.persistence.models.MovementEntity;
import com.demo.trclientes.domain.movement.MovementType;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.MovimientoRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.MovimientoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, ZoneOffset.class})
public interface MovementMapper {

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(target = "account", ignore = true)
    MovementEntity toEntity(Movement domain);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountNumber", source = "account.number")
    @Mapping(target = "accountType", expression = "java(entity.getAccount() != null && entity.getAccount().getTypeAccount() != null ? entity.getAccount().getTypeAccount().name() : null)")
    Movement toDomain(MovementEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", expression = "java(LocalDateTime.now())")
    @Mapping(target = "movementType", source = "tipoMovimiento")
    @Mapping(target = "value", source = "valor")
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "accountNumber", source = "numeroCuenta")
    Movement toDomain(MovimientoRequest request);

    @Mapping(target = "numeroCuenta", source = "accountNumber")
    @Mapping(target = "tipoCuenta", source = "accountType")
    @Mapping(target = "fecha", source = "date")
    @Mapping(target = "estado", constant = "true")
    @Mapping(target = "tipoMovimiento", source = "movementType")
    @Mapping(target = "valor", source = "value")
    @Mapping(target = "saldo", source = "balance")
    MovimientoResponse toResponse(Movement domain);

    @ValueMapping(source = "DEPOSITO", target = "DEPOSITO")
    @ValueMapping(source = "RETIRO", target = "RETIRO")
    MovementType toMovementType(MovimientoRequest.TipoMovimientoEnum tipo);

    default OffsetDateTime map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime value) {
        if (value == null) {
            return null;
        }
        return value.toLocalDateTime();
    }
}
