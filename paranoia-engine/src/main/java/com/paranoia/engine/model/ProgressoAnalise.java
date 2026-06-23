package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Progresso atual da execução do pipeline")
public record ProgressoAnalise(
    @Schema(description = "ID da análise") Long analiseId,
    @Schema(description = "Status atual") StatusAnalise status,
    @Schema(description = "Total de fragilidades encontradas") int totalFragilidades,
    @Schema(description = "Fragilidades já processadas (cenários gerados)") int fragilidadesProcessadas,
    @Schema(description = "Total de cenários gerados") int totalCenarios,
    @Schema(description = "Cenários já executados") int cenariosExecutados,
    @Schema(description = "Data/hora de início") LocalDateTime dataInicio,
    @Schema(description = "Data/hora de fim") LocalDateTime dataFim
) {}
