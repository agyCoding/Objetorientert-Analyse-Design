// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Customer.AccountType;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.services.ActorManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

/**
 * Controller class for the Film Details screen.
 * This class is responsible for displaying the details of a selected film.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

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
    @FXML
    private Button rentButton;
    @FXML
    private Button streamButton;

    private Film selectedFilm;

    public void setSelectedFilm(Film film) {
        // Set the film object to the film object that was clicked
        selectedFilm = film;
        
        // Now that the film is set, update the labels with the film's details
        updateFilmDetails();

        // Set button visibility based on account type
        setButtonVisibility();
    }

    private void updateFilmDetails() {
        // Update the labels based on the film's details
        titleLabel.setText(selectedFilm.getTitle());
        releaseYearLabel.setText("Release year: " + selectedFilm.getReleaseYear());
        descriptionLabel.setText("Film description: " + selectedFilm.getDescription());
    
        // Assuming you have a method to get the language name
        languageLabel.setText("Language: " + selectedFilm.getLanguage().getLanguageName());
        
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
    
    private void setButtonVisibility() {
        // Retrieve the logged-in customer
        Customer loggedInCustomer = SessionData.getInstance().getLoggedInCustomer();
        
        if (loggedInCustomer != null) {
            AccountType accountType = loggedInCustomer.getAccountType();
            
            if (accountType == AccountType.FREE) {
                // Show Rent button and hide Stream button for FREE users
                rentButton.setVisible(true);
                streamButton.setVisible(false);
            } else if (accountType == AccountType.PREMIUM) {
                // Show Stream button and hide Rent button for PREMIUM users
                rentButton.setVisible(false);
                streamButton.setVisible(true);
            }
        }
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
