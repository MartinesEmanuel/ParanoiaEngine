package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cenário de caos gerado pela IA")
public record CenarioCaos(
    @Schema(description = "ID do cenário (preenchido após persistência)") Long id,
    @Schema(description = "Nome curto e descritivo do cenário") String nome,
    @Schema(description = "ID do FragilidadePoint que originou este cenário") Long fragilidadeOrigemId,
    @Schema(description = "Tipo do cenário") TipoCenario tipoCenario,
    @Schema(description = "Descrição técnica do que precisa ser simulado") String descricaoSimulacao,
    @Schema(description = "Comportamento esperado se o código estiver correto") String resultadoEsperado,
    @Schema(description = "Severidade estimada do impacto") SeveridadeEstimada severidadeEstimada
) {}
