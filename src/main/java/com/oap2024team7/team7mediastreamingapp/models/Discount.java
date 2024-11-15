package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

public class Discount {
    private int discountId;
    private int filmId;
    private Double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructor for getting Discount information from the database, with discountId
    public Discount(int discountId, int filmId, Double discountPercentage, LocalDate startDate, LocalDate endDate) {
        this.discountId = discountId;
        this.filmId = filmId;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Simplified constructor for creating a new Discount object, inside the app, without discountId
    public Discount(int filmId, Double discountPercentage, LocalDate startDate, LocalDate endDate) {
        this.filmId = filmId;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
