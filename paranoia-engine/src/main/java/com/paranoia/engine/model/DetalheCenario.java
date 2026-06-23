package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhe de um cenario no relatorio consolidado")
public record DetalheCenario(
    @Schema(description = "Cenario de caos gerado") CenarioCaos cenario,
    @Schema(description = "Ponto de fragilidade de origem") FragilidadePoint fragilidade,
    @Schema(description = "Resultado da execucao (pode ser null se nao executado)") ResultadoExecucao resultado
) {}
