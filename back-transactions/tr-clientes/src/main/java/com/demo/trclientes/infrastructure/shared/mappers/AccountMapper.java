package com.demo.trclientes.infrastructure.shared.mappers;

import com.demo.trclientes.domain.account.AccountType;
import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.infrastructure.persistence.models.AccountEntity;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.CuentaRequest;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.CuentaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "movements", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "number", source = "numberAccount")
    @Mapping(target = "typeAccount", source = "accountType")
    @Mapping(target = "salary", source = "balance")
    AccountEntity toEntity(Account domain);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "numberAccount", source = "number")
    @Mapping(target = "accountType", source = "typeAccount")
    @Mapping(target = "balance", source = "salary")
    Account toDomain(AccountEntity account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numberAccount", source = "numeroCuenta")
    @Mapping(target = "accountType", source = "tipoCuenta", qualifiedByName = "stringToAccountType")
    @Mapping(target = "balance", source = "saldoInicial")
    @Mapping(target = "state", source = "estado")
    @Mapping(target = "clientId", source = "clienteId")
    @Mapping(target = "clientName", ignore = true)
    Account toDomain(CuentaRequest request);

    @Mapping(target = "numeroCuenta", source = "numberAccount")
    @Mapping(target = "tipoCuenta", expression = "java(domain.getAccountType() != null ? domain.getAccountType().name() : null)")
    @Mapping(target = "saldoInicial", source = "balance")
    @Mapping(target = "estado", source = "state")
    @Mapping(target = "clienteId", source = "clientId")
    @Mapping(target = "nombreCliente", source = "clientName")
    CuentaResponse toResponse(Account domain);

    @Named("stringToAccountType")
    default AccountType stringToAccountType(String tipo) {
        if (tipo == null) return null;
        try {
            return AccountType.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
