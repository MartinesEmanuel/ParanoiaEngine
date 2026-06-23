package com.paranoia.engine.controller;

import com.paranoia.engine.execution.AlvoOperacao;
import com.paranoia.engine.execution.CenarioExecutor;
import com.paranoia.engine.execution.ContaServiceExemplo;
import com.paranoia.engine.execution.VerificadorEstado;
import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.ResultadoExecucao;
import com.paranoia.engine.model.TipoCenario;
import com.paranoia.engine.repository.CenarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/execucao")
@Tag(name = "Execucao", description = "Execucao de cenarios de caos")
public class ExecucaoController {

    private final CenarioExecutor cenarioExecutor;
    private final CenarioRepository cenarioRepository;
    private final ContaServiceExemplo contaService;

    public ExecucaoController(CenarioExecutor cenarioExecutor,
                              CenarioRepository cenarioRepository,
                              ContaServiceExemplo contaService) {
        this.cenarioExecutor = cenarioExecutor;
        this.cenarioRepository = cenarioRepository;
        this.contaService = contaService;
    }

    @PostMapping("/{cenarioId}")
    @Operation(summary = "Executar um cenario de caos",
               description = "Dispara a execucao do cenario contra o codigo-alvo. "
               + "Para CONCORRENCIA: threads simultaneas via CountDownLatch. "
               + "Para FALHA_REDE/FALHA_BANCO/TIMEOUT: requer Toxiproxy configurado. "
               + "Este endpoint de exemplo usa ContaServiceExemplo como alvo.")
    public ResponseEntity<ResultadoExecucao> executar(@PathVariable Long cenarioId) {
        CenarioEntity cenario = cenarioRepository.findById(cenarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cenario nao encontrado: " + cenarioId));

        contaService.reset();

        AlvoOperacao operacao;
        if (cenario.getTipoCenario() == TipoCenario.CONCORRENCIA) {
            operacao = () -> contaService.debitarSemLock(100);
        } else {
            operacao = () -> contaService.creditar(100);
        }
        VerificadorEstado verificador = () -> "saldo=" + contaService.getSaldo();

        ResultadoExecucao resultado = cenarioExecutor.executar(cenarioId, operacao, verificador);
        return ResponseEntity.ok(resultado);
    }
}
