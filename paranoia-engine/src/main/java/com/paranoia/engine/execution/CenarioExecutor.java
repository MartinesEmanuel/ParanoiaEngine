package com.paranoia.engine.execution;

import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.ResultadoExecucao;
import com.paranoia.engine.model.ResultadoExecucaoEntity;
import com.paranoia.engine.model.StatusExecucao;
import com.paranoia.engine.model.TipoCenario;
import com.paranoia.engine.repository.CenarioRepository;
import com.paranoia.engine.repository.ResultadoExecucaoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CenarioExecutor {

    private static final Logger log = LoggerFactory.getLogger(CenarioExecutor.class);

    private final CenarioRepository cenarioRepository;
    private final ResultadoExecucaoRepository resultadoRepository;
    private final Optional<ToxiproxyManager> toxiproxyManager;

    public CenarioExecutor(CenarioRepository cenarioRepository,
                           ResultadoExecucaoRepository resultadoRepository,
                           Optional<ToxiproxyManager> toxiproxyManager) {
        this.cenarioRepository = cenarioRepository;
        this.resultadoRepository = resultadoRepository;
        this.toxiproxyManager = toxiproxyManager;
    }

    public ResultadoExecucao executar(Long cenarioId, AlvoOperacao operacao,
                                      VerificadorEstado verificador,
                                      ConfigExecucao config) {
        CenarioEntity cenario = cenarioRepository.findById(cenarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cenario nao encontrado: " + cenarioId));

        return switch (cenario.getTipoCenario()) {
            case CONCORRENCIA -> executarConcorrencia(cenario, operacao, verificador, config);
            case FALHA_REDE, FALHA_BANCO, TIMEOUT ->
                executarComFalha(cenario, operacao, verificador);
        };
    }

    public ResultadoExecucao executar(Long cenarioId, AlvoOperacao operacao,
                                      VerificadorEstado verificador) {
        return executar(cenarioId, operacao, verificador, ConfigExecucao.padrao());
    }

    private ResultadoExecucao executarConcorrencia(CenarioEntity cenario,
                                                   AlvoOperacao operacao,
                                                   VerificadorEstado verificador,
                                                   ConfigExecucao config) {
        log.info("Executando cenario CONCORRENCIA {} ({}) com {} threads",
            cenario.getId(), cenario.getNome(), config.numeroThreads());

        String estadoAntes = verificador.observarEstado();
        List<String> logs = new ArrayList<>();
        logs.add("[%s] Estado antes: %s".formatted(Instant.now(), estadoAntes));

        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);
        List<String> erros = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch gatilho = new CountDownLatch(1);
        CountDownLatch contador = new CountDownLatch(config.numeroThreads());

        ExecutorService pool = Executors.newFixedThreadPool(config.numeroThreads());

        for (int i = 0; i < config.numeroThreads(); i++) {
            final int threadId = i;
            pool.submit(() -> {
                try {
                    gatilho.await();
                    operacao.executar();
                    sucessos.incrementAndGet();
                } catch (Exception e) {
                    falhas.incrementAndGet();
                    erros.add("[Thread %d] %s: %s"
                        .formatted(threadId, e.getClass().getSimpleName(), e.getMessage()));
                } finally {
                    contador.countDown();
                }
            });
        }

        long inicio = System.nanoTime();
        gatilho.countDown();

        try {
            contador.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logs.add("[%s] Execucao interrompida: %s".formatted(Instant.now(), e.getMessage()));
        }

        long duracaoMs = (System.nanoTime() - inicio) / 1_000_000;
        String estadoDepois = verificador.observarEstado();
        logs.add("[%s] Estado depois: %s".formatted(Instant.now(), estadoDepois));
        logs.add("Duracao total: %d ms".formatted(duracaoMs));
        logs.add("Threads sucesso: %d, falhas: %d".formatted(sucessos.get(), falhas.get()));
        logs.addAll(erros);

        pool.shutdown();

        StatusExecucao statusFinal;
        if (falhas.get() > 0 && sucessos.get() > 0) {
            statusFinal = StatusExecucao.QUEBROU;
        } else if (falhas.get() > 0) {
            statusFinal = StatusExecucao.QUEBROU;
        } else {
            statusFinal = StatusExecucao.SOBREVIVEU;
        }

        return salvarResultado(cenario, statusFinal, estadoDepois, logs);
    }

    private ResultadoExecucao executarComFalha(CenarioEntity cenario,
                                               AlvoOperacao operacao,
                                               VerificadorEstado verificador) {
        ToxiproxyManager tox = toxiproxyManager
            .orElseThrow(() -> new IllegalStateException(
                "ToxiproxyManager nao disponivel. Cenarios " + cenario.getTipoCenario()
                + " requerem Toxiproxy em execucao."));

        String proxyNome = "postgres";
        String estadoAntes = verificador.observarEstado();
        List<String> logs = new ArrayList<>();
        logs.add("[%s] Estado antes: %s".formatted(Instant.now(), estadoAntes));

        try {
            tox.aplicarFalha(cenario.getTipoCenario(), proxyNome);
            logs.add("[%s] Toxic aplicado: %s".formatted(Instant.now(), cenario.getTipoCenario()));
        } catch (Exception e) {
            logs.add("[%s] Erro ao aplicar toxic: %s".formatted(Instant.now(), e.getMessage()));
            return salvarResultado(cenario, StatusExecucao.INCONCLUSIVO, estadoAntes, logs);
        }

        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);
        List<String> erros = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch gatilho = new CountDownLatch(1);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.submit(() -> {
            try {
                gatilho.await();
                operacao.executar();
                sucessos.incrementAndGet();
            } catch (Exception e) {
                falhas.incrementAndGet();
                erros.add("%s: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
            }
        });

        long inicio = System.nanoTime();
        gatilho.countDown();
        pool.shutdown();

        boolean terminou;
        try {
            terminou = pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            terminou = false;
            logs.add("[%s] Execucao interrompida".formatted(Instant.now()));
        }

        long duracaoMs = (System.nanoTime() - inicio) / 1_000_000;

        try {
            tox.restaurar(proxyNome);
            logs.add("[%s] Toxic removido".formatted(Instant.now()));
        } catch (Exception e) {
            logs.add("[%s] Erro ao remover toxic: %s".formatted(Instant.now(), e.getMessage()));
        }

        logs.add("Duracao total: %d ms".formatted(duracaoMs));
        logs.add("Sucesso: %d, falhas: %d".formatted(sucessos.get(), falhas.get()));
        logs.addAll(erros);

        String estadoDepois;
        try {
            estadoDepois = verificador.observarEstado();
        } catch (Exception e) {
            estadoDepois = "erro ao observar estado: " + e.getMessage();
        }
        logs.add("[%s] Estado depois: %s".formatted(Instant.now(), estadoDepois));

        StatusExecucao statusFinal;
        if (!terminou) {
            statusFinal = StatusExecucao.INCONCLUSIVO;
        } else if (sucessos.get() == 0 && falhas.get() > 0) {
            statusFinal = StatusExecucao.QUEBROU;
            log.info("Cenario {}: operacao falhou como esperado (timeout/corte)",
                cenario.getId());
        } else if (sucessos.get() > 0 && falhas.get() == 0) {
            statusFinal = StatusExecucao.SOBREVIVEU;
        } else {
            statusFinal = StatusExecucao.INCONCLUSIVO;
        }

        return salvarResultado(cenario, statusFinal, estadoDepois, logs);
    }

    private ResultadoExecucao salvarResultado(CenarioEntity cenario,
                                              StatusExecucao statusFinal,
                                              String estadoDepois,
                                              List<String> logs) {
        log.info("Cenario {} concluido: {}", cenario.getId(), statusFinal);
        try {
            ResultadoExecucaoEntity entity = new ResultadoExecucaoEntity(
                cenario.getId(), statusFinal, estadoDepois,
                cenario.getResultadoEsperado(), logs);
            return resultadoRepository.save(entity).toDto();
        } catch (Exception e) {
            log.warn("Falha ao persistir resultado (conexao foi cortada pelo toxic?): {}",
                e.getMessage());
            return new ResultadoExecucao(null, cenario.getId(), statusFinal, estadoDepois,
                cenario.getResultadoEsperado(), logs);
        }
    }
}
