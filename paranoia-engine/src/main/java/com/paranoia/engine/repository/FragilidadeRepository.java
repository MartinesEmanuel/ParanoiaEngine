package com.paranoia.engine.repository;

import com.paranoia.engine.model.FragilidadeEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FragilidadeRepository extends JpaRepository<FragilidadeEntity, Long> {

    List<FragilidadeEntity> findByAnaliseId(Long analiseId);
}
