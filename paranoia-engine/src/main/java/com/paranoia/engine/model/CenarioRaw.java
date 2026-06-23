package com.paranoia.engine.model;

import java.util.List;

public record CenarioRaw(
    String nome,
    String tipoCenario,
    String descricaoSimulacao,
    String resultadoEsperado,
    String severidadeEstimada
) {}
