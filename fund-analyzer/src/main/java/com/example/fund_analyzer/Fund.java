package com.example.fund_analyzer;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Fund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String code;
    private String name;
    private double price;
    private long shareCount;
    private int peopleCount;
    private double totalPrice;


    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public long getShareCount() {
        return shareCount;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setShareCount(long shareCount) {
        this.shareCount = shareCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
