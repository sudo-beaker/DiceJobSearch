package com.stromsland.dicejobsearch.repository;

import com.stromsland.dicejobsearch.model.DiceJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiceJobRepository extends JpaRepository<DiceJobEntity, String> {
    List<DiceJobEntity> findAllByOrderByPostedDateDesc();
}