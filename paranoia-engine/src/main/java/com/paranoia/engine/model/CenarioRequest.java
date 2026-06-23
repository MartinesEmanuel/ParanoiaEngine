package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Requisição para geração de cenários de caos")
public record CenarioRequest(
    @NotEmpty
    @Schema(description = "Lista de pontos de fragilidade para gerar cenários")
    List<FragilidadePoint> pontos
) {}
