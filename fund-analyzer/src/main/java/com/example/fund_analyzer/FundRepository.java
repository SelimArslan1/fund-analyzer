package com.example.fund_analyzer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FundRepository extends JpaRepository<Fund, Long> {

    List<Fund> findAllByOrderByCodeAscDateAsc();


    Optional<Fund> findById(Long id);

    List<Fund> findAllByCode(String code);

    List<Fund> findAll();
}
