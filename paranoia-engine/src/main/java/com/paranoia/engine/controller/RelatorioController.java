package com.paranoia.engine.controller;

import com.paranoia.engine.model.AnaliseEntity;
import com.paranoia.engine.model.CenarioCaos;
import com.paranoia.engine.model.CenarioEntity;
import com.paranoia.engine.model.DetalheCenario;
import com.paranoia.engine.model.FragilidadeEntity;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.model.RelatorioConsolidado;
import com.paranoia.engine.model.ResultadoExecucao;
import com.paranoia.engine.model.ResultadoExecucaoEntity;
import com.paranoia.engine.model.ResumoGeral;
import com.paranoia.engine.model.SeveridadeEstimada;
import com.paranoia.engine.model.StatusExecucao;
import com.paranoia.engine.repository.AnaliseRepository;
import com.paranoia.engine.repository.CenarioRepository;
import com.paranoia.engine.repository.FragilidadeRepository;
import com.paranoia.engine.repository.ResultadoExecucaoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorio")
@Tag(name = "Relatorio", description = "Relatorio consolidado de execucao de cenarios")
public class RelatorioController {

    private final AnaliseRepository analiseRepository;
    private final FragilidadeRepository fragilidadeRepository;
    private final CenarioRepository cenarioRepository;
    private final ResultadoExecucaoRepository resultadoRepository;

    public RelatorioController(AnaliseRepository analiseRepository,
                               FragilidadeRepository fragilidadeRepository,
                               CenarioRepository cenarioRepository,
                               ResultadoExecucaoRepository resultadoRepository) {
        this.analiseRepository = analiseRepository;
        this.fragilidadeRepository = fragilidadeRepository;
        this.cenarioRepository = cenarioRepository;
        this.resultadoRepository = resultadoRepository;
    }

    @GetMapping("/{analiseId}")
    @Operation(summary = "Obter relatorio consolidado da analise",
               description = "Retorna o relatorio de todos os cenarios gerados a partir "
               + "de todos os pontos de fragilidade de uma analise, com resultados de execucao")
    public ResponseEntity<RelatorioConsolidado> relatorio(@PathVariable Long analiseId) {
        AnaliseEntity analise = analiseRepository.findById(analiseId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Analise nao encontrada: " + analiseId));

        List<FragilidadeEntity> frags = fragilidadeRepository.findByAnaliseId(analiseId);
        List<DetalheCenario> detalhes = new ArrayList<>();

        for (FragilidadeEntity frag : frags) {
            FragilidadePoint fp = new FragilidadePoint(
                frag.getTipo(), frag.getClasseOrigem(),
                frag.getMetodoOrigem(), frag.getLinha(),
                frag.getTrechoCodigo(), frag.getDescricaoTecnica());

            List<CenarioEntity> cenarios = cenarioRepository.findByFragilidadeOrigemId(frag.getId());

            for (CenarioEntity c : cenarios) {
                List<ResultadoExecucaoEntity> resultados =
                    resultadoRepository.findByCenarioId(c.getId());
                ResultadoExecucao ultimoResultado = resultados.isEmpty() ? null
                    : resultados.get(resultados.size() - 1).toDto();

                detalhes.add(new DetalheCenario(c.toDto(), fp, ultimoResultado));
            }
        }

        detalhes.sort(Comparator.comparingInt(
            (DetalheCenario d) -> ordemSeveridade(d.cenario().severidadeEstimada()))
            .thenComparing(d -> d.cenario().id()));

        ResumoGeral resumo = calcularResumo(detalhes);
        return ResponseEntity.ok(new RelatorioConsolidado(resumo, detalhes));
    }

    private ResumoGeral calcularResumo(List<DetalheCenario> detalhes) {
        long total = detalhes.size();
        long sobreviveu = detalhes.stream()
            .filter(d -> d.resultado() != null
                && d.resultado().statusFinal() == StatusExecucao.SOBREVIVEU)
            .count();
        long quebrou = detalhes.stream()
            .filter(d -> d.resultado() != null
                && d.resultado().statusFinal() == StatusExecucao.QUEBROU)
            .count();
        long inconclusivo = detalhes.stream()
            .filter(d -> d.resultado() == null
                || d.resultado().statusFinal() == StatusExecucao.INCONCLUSIVO)
            .count();

        Map<SeveridadeEstimada, Long> dist = new HashMap<>();
        for (SeveridadeEstimada s : SeveridadeEstimada.values()) {
            dist.put(s, detalhes.stream()
                .filter(d -> d.cenario().severidadeEstimada() == s)
                .count());
        }

        return new ResumoGeral(total, sobreviveu, quebrou, inconclusivo, dist);
    }

    private int ordemSeveridade(SeveridadeEstimada s) {
        return switch (s) {
            case CRITICA -> 0;
            case ALTA -> 1;
            case MEDIA -> 2;
            case BAIXA -> 3;
        };
    }
}
