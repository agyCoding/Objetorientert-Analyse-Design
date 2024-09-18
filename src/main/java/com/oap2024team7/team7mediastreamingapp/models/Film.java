package com.oap2024team7.team7mediastreamingapp.models;

public class Film {
    int film_id; // automatically assigned in the database
    String title;
    String description;
    int release_year;
    int language_id;
    int rental_duration; // Understood as MAX ALLOWED rental duration
    int length;

    // Define enum properly at the class level
    public enum Rating { G, PG, PG13, R, NC17 }

    Rating rating; //

    // Constructor for creating a new film from the database
    public Film(int film_id, String title, String description, int release_year, int language_id, int rental_duration, int length, Rating rating) {
        this.film_id = film_id;
        this.title = title;
        this.description = description;
        this.release_year = release_year;
        this.language_id = language_id;
        this.rental_duration = rental_duration;
        this.length = length;
        this.rating = rating;
    }

    // Constructor for creating a new film to be added to the database
    public Film(String title, String description, int release_year, int language_id, int rental_duration, int length, Rating rating) {
        this.title = title;
        this.description = description;
        this.release_year = release_year;
        this.language_id = language_id; // Have to add handling of the language_id based on "regular" language information
        this.rental_duration = rental_duration;
        this.length = length;
        this.rating = rating;
    }

    public int getFilm_id() {
        return film_id;
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

    public int getRelease_year() {
        return release_year;
    }

    public void setRelease_year(int release_year) {
        this.release_year = release_year;
    }

    public int getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public int getRental_duration() {
        return rental_duration;
    }

    public void setRental_duration(int rental_duration) {
        this.rental_duration = rental_duration;
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
