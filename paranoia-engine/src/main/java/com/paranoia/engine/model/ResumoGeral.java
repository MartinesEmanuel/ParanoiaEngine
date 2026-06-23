package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Resumo geral do relatorio consolidado")
public record ResumoGeral(
    @Schema(description = "Total de cenarios analisados") long totalCenarios,
    @Schema(description = "Quantos sobreviveram") long sobreviveu,
    @Schema(description = "Quantos quebraram") long quebrou,
    @Schema(description = "Quantos inconclusivos") long inconclusivo,
    @Schema(description = "Distribuicao por severidade") Map<SeveridadeEstimada, Long> distribuicaoSeveridade
) {}
