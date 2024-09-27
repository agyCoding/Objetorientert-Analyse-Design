package com.oap2024team7.team7mediastreamingapp.controllers;

import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.ActorManager;

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
        releaseYearLabel.setText("Release year: " + selectedFilm.getReleaseYear());
        descriptionLabel.setText("Film description: " + selectedFilm.getDescription());
    
        // Assuming you have a method to get the language name
        languageLabel.setText("Language: " + selectedFilm.getLanguage().getLanguageName());
    
        // Update the actors and special features
        // Placeholder for actors logic
        // actorsLabel.setText("Retrieve actors here");
        
        // Join special features into a single string
        String specialFeatures = String.join(", ", selectedFilm.getSpecialFeatures());
        specialFeaturesLabel.setText("Special features: " + specialFeatures);
    
        // Display the PG rating
        pgRatingLabel.setText("PG rating: " + selectedFilm.getRating().toString());

                // Fetch actors from the database and set them to the film object
        List<Actor> actors = ActorManager.getInstance().getActorsForFilm(selectedFilm.getFilmId());
        StringBuilder actorsText = new StringBuilder("Actors: ");
        for (Actor actor : actors) {
            actorsText.append(actor.getFirstName()).append(" ").append(actor.getLastName()).append(", ");
        }

        // Remove the last comma and space
        if (actorsText.length() > 0) {
            actorsText.setLength(actorsText.length() - 2);
        }
        actorsLabel.setText(actorsText.toString());
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
