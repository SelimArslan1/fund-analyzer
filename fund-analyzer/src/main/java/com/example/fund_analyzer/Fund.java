package com.example.fund_analyzer;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Fund {

    private LocalDate date;
    private String code;
    private String name;
    private float price;
    private int shareCount;
    private int peopleCount;
    private float totalPrice;
}
