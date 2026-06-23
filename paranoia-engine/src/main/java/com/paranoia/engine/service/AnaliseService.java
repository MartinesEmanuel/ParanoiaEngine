package com.paranoia.engine.service;

import com.paranoia.engine.model.FragilidadePoint;

import java.util.List;

public interface AnaliseService {
    List<FragilidadePoint> analisar(String caminhoDiretorio);
}
