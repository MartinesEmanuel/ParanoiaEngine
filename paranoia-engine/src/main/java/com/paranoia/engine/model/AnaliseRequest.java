package com.paranoia.engine.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para iniciar uma nova análise")
public record AnaliseRequest(
    @NotBlank
    @Schema(description = "Caminho do diretório do projeto-alvo a ser analisado")
    String caminhoDiretorio
) {}
