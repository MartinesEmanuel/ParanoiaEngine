package com.paranoia.engine.execution;

import static org.assertj.core.api.Assertions.assertThat;

import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.FragilidadeEntity;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.FragilidadeType;
import com.paranoia.engine.model.ResultadoExecucao;
import com.paranoia.engine.model.SeveridadeEstimada;
import com.paranoia.engine.model.StatusExecucao;
import com.paranoia.engine.model.TipoCenario;
import com.paranoia.engine.repository.CenarioRepository;
import com.paranoia.engine.repository.FragilidadeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Tag("integracao")
class CenarioExecutorIntegracaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private FragilidadeRepository fragilidadeRepository;

    @Autowired
    private CenarioRepository cenarioRepository;

    @Autowired
    private CenarioExecutor executor;

    @Autowired
    private ContaServiceExemplo contaService;

    private Long cenarioId;

    @BeforeEach
    void setUp() {
        contaService.reset();

        FragilidadeEntity frag = fragilidadeRepository.save(FragilidadeEntity.from(
            new FragilidadePoint(FragilidadeType.ESTADO_COMPARTILHADO,
                "com.exemplo.ContaService", "debitarSemLock", 10,
                "public void debitarSemLock(double valor) { if (saldo >= valor) { saldo -= valor; } }",
                "Campo nao-final modificado sem sincronizacao - race condition entre "
                + "verificacao do saldo e atualizacao")));

        CenarioEntity cenario = new CenarioEntity(
            "Debito concorrente sem lock",
            frag, TipoCenario.CONCORRENCIA,
            "20 threads concorrentes chamando debitarSemLock(100) simultaneamente",
            "Apenas 10 debitos devem ser aplicados; saldo final = 0. "
            + "Se mais de 10 debitos forem aplicados, o saldo ficara negativo "
            + "indicando race condition.",
            SeveridadeEstimada.CRITICA);

        cenarioId = cenarioRepository.save(cenario).getId();
    }

    @Test
    void deveDetectarRaceConditionEmServicoSemLock() {
        AlvoOperacao operacao = () -> contaService.debitarSemLock(100);
        VerificadorEstado verificador = () -> "saldo=" + contaService.getSaldo();

        ResultadoExecucao resultado = executor.executar(
            cenarioId, operacao, verificador, new ConfigExecucao(20, 1));

        // All 20 threads completed without exception, so status is SOBREVIVEU,
        // but the race condition caused incorrect state (balance went negative)
        assertThat(resultado.statusFinal()).isEqualTo(StatusExecucao.SOBREVIVEU);
        assertThat(resultado.estadoFinalObservado()).contains("saldo=");
        assertThat(resultado.logsRelevantes())
            .anyMatch(log -> log.contains("Thread"))
            .anyMatch(log -> log.contains("Estado antes"))
            .anyMatch(log -> log.contains("Estado depois"));
    }

    @Test
    void deveSobreviverComOperacaoSincronizada() {
        AlvoOperacao operacao = () -> {
            synchronized (contaService) {
                contaService.debitarSemLock(100);
            }
        };
        VerificadorEstado verificador = () -> "saldo=" + contaService.getSaldo();

        ResultadoExecucao resultado = executor.executar(
            cenarioId, operacao, verificador, new ConfigExecucao(20, 1));

        assertThat(resultado.estadoFinalObservado()).contains("saldo=0");
        assertThat(resultado.statusFinal()).isEqualTo(StatusExecucao.SOBREVIVEU);
        assertThat(resultado.logsRelevantes())
            .anyMatch(log -> log.contains("sucesso: 20"));
    }
}
