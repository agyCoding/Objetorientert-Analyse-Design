// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller class for the rent film view
 * This class is responsible for handling the user input and handling the renting of the selected film
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class RentFilmController {
    @FXML
    private Label selectedFilmLabel;
    @FXML
    private Label maxRentalLengthLabel;
    @FXML
    private Label rentalRateLabel;
    @FXML
    private TextField rentalDaysTF;
    @FXML
    private Label totalCostLabel;

    private Film selectedFilm;

    @FXML
    private void initialize() {
        // Get the selected film from the session data
        selectedFilm = SessionData.getInstance().getSelectedFilm();

        // Display the selected film's details
        String filmString = selectedFilm.getTitle() + " (" + selectedFilm.getReleaseYear() + ")";
        selectedFilmLabel.setText(filmString);
        maxRentalLengthLabel.setText("Max rental duration: " + String.valueOf(selectedFilm.getRentalDuration()));
        rentalRateLabel.setText("Rental rate: " + String.valueOf(selectedFilm.getRentalRate()));
    }

    @FXML
    private void tryToRent() {
        // Get the number of rental days from the text field
        int rentalDays;
        try {
            rentalDays = Integer.parseInt(rentalDaysTF.getText());
        } catch (NumberFormatException e) {
            GeneralUtils.showAlert(AlertType.ERROR,"Invalid input", "Please enter a valid number of days.","You need to enter numerical value.");
            return;
        }

        // Calculate the total cost
        double totalCost = selectedFilm.getRentalRate() * rentalDays;
        totalCostLabel.setText("Total cost: " + String.valueOf(totalCost));
    }
}