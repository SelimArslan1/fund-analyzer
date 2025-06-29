package com.example.fund_analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
public class FundStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fundCode;

    private LocalDate startDate;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode pattern;


    public Long getId() {
        return id;
    }

    public String getFundCode() {
        return fundCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public JsonNode getPattern() {
        return pattern;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setPattern(JsonNode pattern) {
        this.pattern = pattern;
    }
}
