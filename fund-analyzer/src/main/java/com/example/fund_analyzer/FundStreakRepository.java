package com.example.fund_analyzer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundStreakRepository extends JpaRepository<FundStreak, Long> {
    List<FundStreak> findAll();
}
