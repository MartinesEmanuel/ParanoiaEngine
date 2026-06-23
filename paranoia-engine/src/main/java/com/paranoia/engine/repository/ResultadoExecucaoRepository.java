package com.paranoia.engine.repository;

import com.paranoia.engine.model.ResultadoExecucaoEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultadoExecucaoRepository extends JpaRepository<ResultadoExecucaoEntity, Long> {

    List<ResultadoExecucaoEntity> findByCenarioId(Long cenarioId);
}
