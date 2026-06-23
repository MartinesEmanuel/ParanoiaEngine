package com.paranoia.engine.controller;

import com.paranoia.engine.model.AnaliseEntity;
import com.paranoia.engine.model.AnaliseRequest;
import com.paranoia.engine.model.AnaliseResponse;
import com.paranoia.engine.model.FragilidadeEntity;
import com.paranoia.engine.model.FragilidadePoint;
import com.paranoia.engine.repository.FragilidadeRepository;
import com.paranoia.engine.service.OrquestradorService;

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
@RequestMapping("/api/analise")
@Tag(name = "Analise", description = "Analise estatica de codigo-alvo")
public class AnaliseController {

    private final OrquestradorService orquestradorService;
    private final FragilidadeRepository fragilidadeRepository;

    public AnaliseController(OrquestradorService orquestradorService,
                             FragilidadeRepository fragilidadeRepository) {
        this.orquestradorService = orquestradorService;
        this.fragilidadeRepository = fragilidadeRepository;
    }

    @PostMapping
    @Operation(summary = "Iniciar analise estatica",
               description = "Analisa o diretorio do projeto-alvo e retorna os "
               + "pontos de fragilidade encontrados, agrupados em uma analise")
    @ApiResponse(responseCode = "200", description = "Analise concluida com sucesso")
    public ResponseEntity<AnaliseResponse> analisar(
            @Valid @RequestBody AnaliseRequest request) {
        AnaliseEntity analise = orquestradorService.criarAnalise(request.caminhoDiretorio());
        List<FragilidadeEntity> frags = fragilidadeRepository.findByAnaliseId(analise.getId());
        List<FragilidadePoint> pontos = frags.stream()
            .map(f -> new FragilidadePoint(f.getTipo(), f.getClasseOrigem(),
                f.getMetodoOrigem(), f.getLinha(), f.getTrechoCodigo(),
                f.getDescricaoTecnica()))
            .toList();

        AnaliseResponse response = new AnaliseResponse(
            analise.getId(), analise.getStatus(),
            analise.getCaminhoDiretorio(), analise.getDataInicio(),
            analise.getDataFim(), pontos);

        return ResponseEntity.ok(response);
    }
}
