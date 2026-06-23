package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Relatorio consolidado de todos os cenarios de uma analise")
public record RelatorioConsolidado(
    @Schema(description = "Resumo geral") ResumoGeral resumo,
    @Schema(description = "Cenarios ordenados por severidade (CRITICA primeiro)") List<DetalheCenario> cenarios
) {}
