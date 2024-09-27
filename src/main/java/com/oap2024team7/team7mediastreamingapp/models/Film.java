package com.oap2024team7.team7mediastreamingapp.models;



public class Film {
    private int filmId; // automatically assigned in the database
    private String title;
    private String description;
    private int releaseYear;
    private Language language;
    private int rentalDuration; // Understood as MAX ALLOWED rental duration
    private int length;

    // Define enum properly at the class level
    public enum Rating { G, PG, PG13, R, NC17 }

    private Rating rating;

    // Constructor for creating a new film from the database
    public Film(int filmId, String title, String description, int releaseYear, Language language, int rentalDuration, int length, Rating rating) {
        this.filmId = filmId;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.language = language;
        this.rentalDuration = rentalDuration;
        this.length = length;
        this.rating = rating;
    }

    // Constructor for creating a new film to be added to the database
    public Film(String title, String description, int releaseYear, Language language, int rentalDuration, int length, Rating rating) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.language = language;
        this.rentalDuration = rentalDuration;
        this.length = length;
        this.rating = rating;
    }

    public int getFilmId() {
        return filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguageId(Language language) {
        this.language = language;
    }

    public int getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(int rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}
