package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado da execucao de um cenario de caos")
public record ResultadoExecucao(
    @Schema(description = "ID do resultado") Long id,
    @Schema(description = "ID do cenario executado") Long cenarioId,
    @Schema(description = "Status final apos a execucao") StatusExecucao statusFinal,
    @Schema(description = "Estado final observado (ex: saldo final no banco)") String estadoFinalObservado,
    @Schema(description = "Estado esperado conforme o cenario") String estadoEsperado,
    @Schema(description = "Logs relevantes da execucao") List<String> logsRelevantes
) {}
