package com.paranoia.engine.controller;

import com.paranoia.engine.execution.AlvoOperacao;
import com.paranoia.engine.execution.VerificadorEstado;
import com.paranoia.engine.model.AnaliseEntity;
import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.ProgressoAnalise;
import com.paranoia.engine.model.StatusAnalise;
import com.paranoia.engine.repository.AnaliseRepository;
import com.paranoia.engine.service.OrquestradorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.function.Function;

@RestController
@RequestMapping("/api/executar-tudo")
@Tag(name = "Pipeline", description = "Orquestracao automatica do pipeline completo")
public class ExecutarTudoController {

    private final OrquestradorService orquestradorService;
    private final AnaliseRepository analiseRepository;
    private final Function<CenarioEntity, AlvoOperacao> provedorOperacao;
    private final Function<CenarioEntity, VerificadorEstado> provedorVerificador;

    public ExecutarTudoController(OrquestradorService orquestradorService,
                                  AnaliseRepository analiseRepository,
                                  @Autowired(required = false) Function<CenarioEntity, AlvoOperacao> provedorOperacao,
                                  @Autowired(required = false) Function<CenarioEntity, VerificadorEstado> provedorVerificador) {
        this.orquestradorService = orquestradorService;
        this.analiseRepository = analiseRepository;
        this.provedorOperacao = provedorOperacao;
        this.provedorVerificador = provedorVerificador;
    }

    @PostMapping("/{analiseId}")
    @Operation(summary = "Executar pipeline completo",
               description = "Dispara a execucao completa do pipeline em background: "
               + "gera cenarios via IA para cada fragilidade e executa cada cenario. "
               + "Retorna 202 Accepted imediatamente. Consulte GET /status para progresso.")
    @ApiResponse(responseCode = "202", description = "Pipeline iniciado em background")
    public ResponseEntity<Void> executarTudo(@PathVariable Long analiseId) {
        AnaliseEntity analise = analiseRepository.findById(analiseId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Analise nao encontrada: " + analiseId));

        if (analise.getStatus() == StatusAnalise.EM_ANDAMENTO) {
            return ResponseEntity.accepted()
                .location(URI.create("/api/executar-tudo/" + analiseId + "/status"))
                .build();
        }

        orquestradorService.executarPipeline(analiseId, provedorOperacao, provedorVerificador);

        return ResponseEntity.accepted()
            .location(URI.create("/api/executar-tudo/" + analiseId + "/status"))
            .build();
    }

    @GetMapping("/{analiseId}/status")
    @Operation(summary = "Consultar progresso do pipeline",
               description = "Retorna o status atual e contadores de progresso da execucao")
    public ResponseEntity<ProgressoAnalise> status(@PathVariable Long analiseId) {
        AnaliseEntity analise = analiseRepository.findById(analiseId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Analise nao encontrada: " + analiseId));

        ProgressoAnalise progresso = new ProgressoAnalise(
            analise.getId(), analise.getStatus(),
            analise.getTotalFragilidades(), analise.getFragilidadesProcessadas(),
            analise.getTotalCenarios(), analise.getCenariosExecutados(),
            analise.getDataInicio(), analise.getDataFim());

        return ResponseEntity.ok(progresso);
    }
}
