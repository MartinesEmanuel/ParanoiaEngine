package com.paranoia.engine.service;

import com.paranoia.engine.execution.AlvoOperacao;
import com.paranoia.engine.execution.CenarioExecutor;
import com.paranoia.engine.execution.VerificadorEstado;
import com.paranoia.engine.model.AnaliseEntity;
import com.paranoia.engine.model.CenarioCaos;
import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.FragilidadeEntity;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.repository.AnaliseRepository;
import com.paranoia.engine.repository.CenarioRepository;
import com.paranoia.engine.repository.FragilidadeRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class OrquestradorService {

    private static final Logger log = LoggerFactory.getLogger(OrquestradorService.class);

    private final AnaliseRepository analiseRepository;
    private final FragilidadeRepository fragilidadeRepository;
    private final CenarioRepository cenarioRepository;
    private final AnaliseService analiseService;
    private final CenarioIaService cenarioIaService;
    private final CenarioExecutor cenarioExecutor;

    public OrquestradorService(AnaliseRepository analiseRepository,
                               FragilidadeRepository fragilidadeRepository,
                               CenarioRepository cenarioRepository,
                               AnaliseService analiseService,
                               CenarioIaService cenarioIaService,
                               CenarioExecutor cenarioExecutor) {
        this.analiseRepository = analiseRepository;
        this.fragilidadeRepository = fragilidadeRepository;
        this.cenarioRepository = cenarioRepository;
        this.analiseService = analiseService;
        this.cenarioIaService = cenarioIaService;
        this.cenarioExecutor = cenarioExecutor;
    }

    public AnaliseEntity criarAnalise(String caminhoDiretorio) {
        AnaliseEntity analise = new AnaliseEntity(caminhoDiretorio);
        analise = analiseRepository.save(analise);

        try {
            List<FragilidadePoint> pontos = analiseService.analisar(caminhoDiretorio);
            for (FragilidadePoint p : pontos) {
                FragilidadeEntity frag = FragilidadeEntity.from(p);
                frag.setAnalise(analise);
                fragilidadeRepository.save(frag);
            }
            analise.setTotalFragilidades(pontos.size());
            analiseRepository.save(analise);
            log.info("Analise {} criada com {} fragilidades", analise.getId(), pontos.size());
        } catch (Exception e) {
            log.error("Falha na analise estatica {}: {}", analise.getId(), e.getMessage());
            analise.falhar();
            analiseRepository.save(analise);
        }

        return analise;
    }

    @Async("pipelineExecutor")
    public void executarPipeline(Long analiseId,
                                 Function<CenarioEntity, AlvoOperacao> provedorOperacao,
                                 Function<CenarioEntity, VerificadorEstado> provedorVerificador) {
        log.info("=== Pipeline iniciado para analise {} ===", analiseId);
        AnaliseEntity analise = analiseRepository.findById(analiseId)
            .orElseThrow(() -> new IllegalArgumentException("Analise nao encontrada: " + analiseId));

        List<FragilidadeEntity> fragilidades = fragilidadeRepository.findByAnaliseId(analiseId);
        log.info("Processando {} fragilidades", fragilidades.size());

        int fragCount = 0;
        int cenarioCount = 0;

        for (FragilidadeEntity frag : fragilidades) {
            try {
                List<CenarioCaos> cenarios = cenarioIaService.gerarCenarios(frag);
                cenarioCount += cenarios.size();
                fragCount++;
                analise.setFragilidadesProcessadas(fragCount);
                analiseRepository.save(analise);
                log.info("Fragilidade {}/{}: {} cenarios gerados",
                    fragCount, fragilidades.size(), cenarios.size());

                for (CenarioCaos cenario : cenarios) {
                    if (provedorOperacao == null || provedorVerificador == null) {
                        log.info("Cenario {} gerado (execucao pendente - sem alvo configurado)",
                            cenario.id());
                        continue;
                    }

                    try {
                        CenarioEntity cenarioEntity = cenarioRepository.findById(cenario.id())
                            .orElse(null);
                        if (cenarioEntity == null) continue;

                        AlvoOperacao operacao = provedorOperacao.apply(cenarioEntity);
                        VerificadorEstado verificador = provedorVerificador.apply(cenarioEntity);

                        cenarioExecutor.executar(cenario.id(), operacao, verificador);
                        log.info("Cenario {} executado", cenario.id());
                    } catch (Exception e) {
                        log.warn("Falha ao executar cenario {}: {}", cenario.id(), e.getMessage());
                    }

                    analise.setCenariosExecutados(++cenarioCount > analise.getTotalCenarios()
                        ? cenarioCount : cenarioCount);
                    analiseRepository.save(analise);
                }

            } catch (Exception e) {
                log.warn("Falha ao processar fragilidade {}#{}: {}",
                    frag.getClasseOrigem(), frag.getMetodoOrigem(), e.getMessage());
                fragCount++;
                analise.setFragilidadesProcessadas(fragCount);
                analiseRepository.save(analise);
            }
        }

        analise.setTotalCenarios(cenarioCount);
        analise.concluir();
        analiseRepository.save(analise);
        log.info("=== Pipeline concluido para analise {} ({} cenarios, duracao {}s) ===",
            analiseId, cenarioCount,
            java.time.Duration.between(analise.getDataInicio(), LocalDateTime.now()).toSeconds());
    }

    @Async("pipelineExecutor")
    public void executarPipeline(Long analiseId) {
        executarPipeline(analiseId, null, null);
    }
}
