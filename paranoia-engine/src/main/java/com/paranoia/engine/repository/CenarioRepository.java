package com.paranoia.engine.repository;

import com.paranoia.engine.model.CenarioEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenarioRepository extends JpaRepository<CenarioEntity, Long> {

    List<CenarioEntity> findByFragilidadeOrigemId(Long fragilidadeId);
}
