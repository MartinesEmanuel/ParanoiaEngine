package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ponto de fragilidade identificado no código-alvo")
public record FragilidadePoint(
    @Schema(description = "Tipo da fragilidade") FragilidadeType tipo,
    @Schema(description = "Nome completo da classe onde foi encontrada") String classeOrigem,
    @Schema(description = "Nome do método (pode ser null para fragilidades em nível de classe)") String metodoOrigem,
    @Schema(description = "Número da linha no arquivo original") int linha,
    @Schema(description = "Snippet do código relevante (até 10 linhas)") String trechoCodigo,
    @Schema(description = "Explicação técnica do motivo da fragilidade") String descricaoTecnica
) {}
