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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import eu.rekawek.toxiproxy.ToxiproxyClient;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Tag("integracao")
class CenarioExecutorCompletoIntegracaoTest {

    private static final String POSTGRES_PROXY = "postgres";
    private static final String POSTGRES_ALIAS = "postgres";

    static Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withNetwork(network)
        .withNetworkAliases(POSTGRES_ALIAS);

    @Container
    static ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.8.0")
        .withNetwork(network);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        try {
            var client = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
            var cp = toxiproxy.getProxy(postgres, 5432);
            int proxyPort = cp.getOriginalProxyPort();
            client.getProxy(cp.getName()).delete();
            client.createProxy(POSTGRES_PROXY, "0.0.0.0:" + proxyPort, POSTGRES_ALIAS + ":5432");
            String host = toxiproxy.getHost();
            int port = toxiproxy.getMappedPort(proxyPort);
            registry.add("spring.datasource.url",
                () -> "jdbc:postgresql://" + host + ":" + port + "/test?sslmode=disable");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar proxy Toxiproxy", e);
        }
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.datasource.hikari.connection-test-query", () -> "SELECT 1");
        registry.add("spring.datasource.hikari.max-lifetime", () -> "5000");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        ToxiproxyManager toxiproxyManager() {
            String host = toxiproxy.getHost();
            Integer port = toxiproxy.getControlPort();
            return new ToxiproxyManagerImpl(host + ":" + port, POSTGRES_PROXY);
        }
    }

    @Autowired
    private FragilidadeRepository fragilidadeRepository;

    @Autowired
    private CenarioRepository cenarioRepository;

    @Autowired
    private CenarioExecutor executor;

    @Autowired
    private ContaServiceExemplo contaService;

    @Autowired
    private ToxiproxyManager toxiproxyManager;

    @Autowired
    private DataSource dataSource;

    private FragilidadeEntity frag;

    @BeforeEach
    void setUp() {
        ((HikariDataSource) dataSource).getHikariPoolMXBean().softEvictConnections();
        contaService.reset();
        frag = fragilidadeRepository.save(FragilidadeEntity.from(
            new FragilidadePoint(FragilidadeType.TRANSACAO,
                "com.exemplo.ContaService", "debitar", 15,
                """
                @Transactional
                public void debitar(double valor) {
                    if (saldo >= valor) {
                        saldo -= valor;
                    }
                }
                """,
                "Metodo transacional sem protecao contra concorrencia")));
        limparToxics();
    }

    @AfterEach
    void cleanup() {
        limparToxics();
    }

    @Test
    void deveDetectarFalhaRede() {
        Long cenarioId = criarCenario(TipoCenario.FALHA_REDE, SeveridadeEstimada.ALTA,
            "Timeout na conexao com banco durante operacao",
            "Simular queda de rede: aplicar toxic de timeout de 1ms na conexao "
            + "com o banco durante a execucao do metodo.",
            "A aplicacao deve capturar a excecao de conexao e fazer rollback "
            + "completo da transacao, sem corromper estado.");

        AlvoOperacao op = () -> contaService.creditar(100);
        VerificadorEstado verif = () -> "saldo=" + contaService.getSaldo();

        ResultadoExecucao resultado = executor.executar(cenarioId, op, verif);
        assertThat(resultado.statusFinal()).isEqualTo(StatusExecucao.SOBREVIVEU);
        assertThat(resultado.logsRelevantes())
            .anyMatch(log -> log.contains("Toxic aplicado"))
            .anyMatch(log -> log.contains("Toxic removido"));
    }

    @Test
    void deveDetectarFalhaBanco() {
        Long cenarioId = criarCenario(TipoCenario.FALHA_BANCO, SeveridadeEstimada.CRITICA,
            "Queda do banco no meio da transacao",
            "Simular queda abrupta da conexao com PostgreSQL durante a execucao "
            + "de creditar(), apos o calculo do novo saldo mas antes do flush.",
            "A transacao deve reverter o calculo do saldo; o saldo permanece "
            + "inalterado (1000).");

        AlvoOperacao op = () -> contaService.creditar(100);
        VerificadorEstado verif = () -> "saldo=" + contaService.getSaldo();

        ResultadoExecucao resultado = executor.executar(cenarioId, op, verif);
        assertThat(resultado.logsRelevantes())
            .anyMatch(log -> log.contains("Toxic aplicado"))
            .anyMatch(log -> log.contains("Toxic removido"));
    }

    @Test
    void deveDetectarTimeout() {
        Long cenarioId = criarCenario(TipoCenario.TIMEOUT, SeveridadeEstimada.MEDIA,
            "Timeout na operacao de banco",
            "Simular latencia excessiva de 30s na conexao com banco durante "
            + "chamada a creditar(), excedendo o timeout configurado.",
            "A aplicacao deve lancar excecao de timeout tratada e nao travar "
            + "indefinidamente.");

        AlvoOperacao op = () -> contaService.creditar(100);
        VerificadorEstado verif = () -> "saldo=" + contaService.getSaldo();

        ResultadoExecucao resultado = executor.executar(cenarioId, op, verif);
        assertThat(resultado.logsRelevantes())
            .anyMatch(log -> log.contains("Toxic aplicado"))
            .anyMatch(log -> log.contains("Toxic removido"));
    }

    private Long criarCenario(TipoCenario tipo, SeveridadeEstimada severidade,
                              String nome, String descricao, String resultadoEsperado) {
        CenarioEntity cenario = new CenarioEntity(nome, frag, tipo,
            descricao, resultadoEsperado, severidade);
        return cenarioRepository.save(cenario).getId();
    }

    private void limparToxics() {
        try {
            var client = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
            var proxy = client.getProxyOrNull(POSTGRES_PROXY);
            if (proxy != null) {
                for (var toxic : proxy.toxics().getAll()) {
                    toxic.remove();
                }
            }
        } catch (Exception ignored) {}
    }
}
