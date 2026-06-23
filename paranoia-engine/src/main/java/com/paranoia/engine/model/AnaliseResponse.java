package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta da criação de uma análise")
public record AnaliseResponse(
    @Schema(description = "ID da análise") Long id,
    @Schema(description = "Status atual") StatusAnalise status,
    @Schema(description = "Caminho do diretório analisado") String caminhoDiretorio,
    @Schema(description = "Data/hora de início") LocalDateTime dataInicio,
    @Schema(description = "Data/hora de fim (pode ser null se em andamento)") LocalDateTime dataFim,
    @Schema(description = "Pontos de fragilidade encontrados") List<FragilidadePoint> pontos
) {}
