package com.paranoia.engine.controller;

import com.paranoia.engine.model.CenarioCaos;
import com.paranoia.engine.model.CenarioRequest;
import com.paranoia.engine.service.CenarioIaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cenarios")
@Tag(name = "Cenários", description = "Geração de cenários de caos via IA")
public class CenarioController {

    private final CenarioIaService cenarioIaService;

    public CenarioController(CenarioIaService cenarioIaService) {
        this.cenarioIaService = cenarioIaService;
    }

    @PostMapping
    @Operation(summary = "Gerar cenários de caos",
               description = "Para cada ponto de fragilidade, gera 3 cenários de caos específicos usando IA")
    @ApiResponse(responseCode = "200", description = "Cenários gerados com sucesso")
    @ApiResponse(responseCode = "400", description = "Lista de pontos inválida")
    public ResponseEntity<List<CenarioCaos>> gerarCenarios(
            @Valid @RequestBody CenarioRequest request) {
        List<CenarioCaos> cenarios = cenarioIaService.gerarCenarios(request.pontos());
        return ResponseEntity.ok(cenarios);
    }
}
