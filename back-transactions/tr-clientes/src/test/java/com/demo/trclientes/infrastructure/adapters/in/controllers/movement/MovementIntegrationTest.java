package com.demo.trclientes.infrastructure.adapters.in.controllers.movement;

import com.demo.trclientes.domain.account.AccountType;
import com.demo.trclientes.domain.account.Account;
import com.demo.trclientes.domain.account.ports.out.AccountRepositoryPort;
import com.demo.trclientes.domain.client.models.Client;
import com.demo.trclientes.domain.client.ports.out.ClientRepositoryPort;
import com.demo.trclientes.infrastructure.adapters.in.rest.dtos.MovimientoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MovementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepositoryPort accountRepository;

    @Autowired
    private ClientRepositoryPort clientRepository;

    private static final String ACCOUNT_NUMBER = "111222";
    private static final String CLIENT_ID = "client_test_001";
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000.00");

    @BeforeEach
    void setup() {
        Client client = Client.builder()
                .name("John Doe")
                .clientId(CLIENT_ID)
                .password("1234")
                .active(true)
                .build();
        
        client = clientRepository.save(client);

        accountRepository.save(Account.builder()
                .numberAccount(ACCOUNT_NUMBER)
                .accountType(AccountType.AHORRO)
                .balance(INITIAL_BALANCE)
                .state(true)
                .clientId(client.getId())
                .build());
    }

    @Test
    @DisplayName("POST /movimientos - Create Deposit")
    void createMovement_ShouldCreateDepositSuccessfully() throws Exception {
        BigDecimal depositValue = new BigDecimal("500.00");
        BigDecimal expectedFinalBalance = INITIAL_BALANCE.add(depositValue);

        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta(ACCOUNT_NUMBER);
        request.setTipoMovimiento(MovimientoRequest.TipoMovimientoEnum.DEPOSITO);
        request.setValor(depositValue);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.data.numeroCuenta", is(ACCOUNT_NUMBER)))
                .andExpect(jsonPath("$.data.valor", is(depositValue.doubleValue())))
                .andExpect(jsonPath("$.data.saldo", is(expectedFinalBalance.doubleValue())));
    }

    @Test
    @DisplayName("POST /movimientos - Create Withdrawal")
    void createMovement_ShouldCreateWithdrawalSuccessfully() throws Exception {
        BigDecimal withdrawalValue = new BigDecimal("200.00");
        BigDecimal expectedFinalBalance = INITIAL_BALANCE.subtract(withdrawalValue);

        MovimientoRequest request = new MovimientoRequest();
        request.setNumeroCuenta(ACCOUNT_NUMBER);
        request.setTipoMovimiento(MovimientoRequest.TipoMovimientoEnum.RETIRO);
        request.setValor(withdrawalValue);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.data.numeroCuenta", is(ACCOUNT_NUMBER)))
                .andExpect(jsonPath("$.data.valor", is(withdrawalValue.negate().doubleValue())))
                .andExpect(jsonPath("$.data.saldo", is(expectedFinalBalance.doubleValue())));
    }
}
