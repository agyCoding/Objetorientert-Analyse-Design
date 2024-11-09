package com.oap2024team7.team7mediastreamingapp.models;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;

/**
 * Class for the Film object.
 * This class is responsible for creating and managing Film objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class Film {
    private int filmId; // automatically assigned in the database
    private String title;
    private String description;
    private int releaseYear;
    private Language language;
    private int rentalDuration; // Understood as MAX ALLOWED rental duration
    private Double rentalRate; // Understood as the cost of renting the film per day
    private int length;
    private Set<String> specialFeatures; // Set of special features that come with the film
    private List<Actor> actors; // List of actors in the film

    // Define enum at the class level
    public enum Rating { G, PG, PG13, R, NC17 }

    private Rating rating;

    // Static set to hold all possible special features
    private static Set<String> predefinedSpecialFeatures;

    // Static block to initialize the predefined special features set
    static {
        Set<String> features = new HashSet<>();
        features.add("Trailers");
        features.add("Commentaries");
        features.add("Deleted Scenes");
        features.add("Behind the Scenes");
        predefinedSpecialFeatures = Collections.unmodifiableSet(features);
    }

    // Getter for predefinedSpecialFeatures
    public static Set<String> getPredefinedSpecialFeatures() {
        return predefinedSpecialFeatures;
    }

    // Constructor for creating a new film from the database
    public Film(int filmId, String title, String description, int releaseYear, Language language, int rentalDuration, int length, Rating rating, Set<String> specialFeatures, Double rentalRate, List<Actor> actors) {
        this.filmId = filmId;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.language = language;
        this.rentalDuration = rentalDuration;
        this.length = length;
        this.rating = rating;
        this.specialFeatures = specialFeatures;
        this.rentalRate = rentalRate;
        this.actors = actors;
    }

    // Constructor for creating a new film to be added to the database
    public Film(String title, String description, int releaseYear, Language language, int rentalDuration, int length, Rating rating, Set<String> specialFeatures, Double rentalRate) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.language = language;
        this.rentalDuration = rentalDuration;
        this.length = length;
        this.rating = rating;
        this.specialFeatures = specialFeatures;
        this.rentalRate = rentalRate;
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

    public void setLanguage(Language language) {
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

    public Set<String> getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(Set<String> specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public Double getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(Double rentalRate) {
        this.rentalRate = rentalRate;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
}
