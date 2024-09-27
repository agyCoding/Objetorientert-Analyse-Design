package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FilmDetailsController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label releaseYearLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label actorsLabel;
    @FXML
    private Label specialFeaturesLabel;
    @FXML
    private Label pgRatingLabel;

    private Film selectedFilm;

    public void setSelectedFilm(Film film) {
        // Set the film object to the film object that was clicked
        selectedFilm = film;
        
        // Now that the film is set, update the labels with the film's details
        updateFilmDetails();
    }

    private void updateFilmDetails() {
        // Update the labels based on the film's details
        titleLabel.setText(selectedFilm.getTitle());
        releaseYearLabel.setText(String.valueOf(selectedFilm.getReleaseYear()));
        descriptionLabel.setText(selectedFilm.getDescription());

        // Implement logic to map the languageId to the actual language name
        languageLabel.setText(String.valueOf(selectedFilm.getLanguage().getLanguageName()));

        // Placeholder for actors and special features logic
        // actorsLabel.setText("Retrieve actors here");
        // specialFeaturesLabel.setText(selectedFilm.getSpecialFeatures());

        pgRatingLabel.setText(selectedFilm.getRating().toString());
    }

    // TO DO
    @FXML
    private void showRentWindow() {
        // Show rent window for the selected film
        System.out.println("Rent window for film: " + selectedFilm.getTitle());
    }

    // TO DO
    @FXML
    private void tryToStream() {
        // Try to stream the selected film
        System.out.println("Trying to stream film: " + selectedFilm.getTitle());
    }
}
