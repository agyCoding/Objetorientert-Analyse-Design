package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Customer.AccountType;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.ActorManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

/**
 * Controller class for the Film Details screen.
 * This class is responsible for displaying the details of a selected film.
 * 
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
    @FXML
    private Button saveToListButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Button likeButton;
    @FXML
    private Button dislikeButton;
    @FXML
    private Label avgScoreLabel;

    private Film selectedFilm;
    private Stage stage;
    private FilmManager filmManager;
   
    /**
     * The stage for the Film Details window.
     * This is used to set a listener to clear the selected film when the window is closed.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        // Set a listener to clear the selectedFilm when the window is closed
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Clear the selected film from SessionData
                SessionData.getInstance().setSelectedFilm(null);
            }
        });
    }

    @FXML
    public void initialize() {
        // Retrieve the selected film from the session data
        selectedFilm = SessionData.getInstance().getSelectedFilm();
        filmManager = new FilmManager();
    
        if (selectedFilm != null) {
            // Now that the film is set, update the labels with the film's details
            updateFilmDetails();
    
            // Set button visibility based on account type
            setButtonVisibility();
    
            // Update the status label based on whether the film is already in the list
            if (SessionData.getInstance().getSavedFilms().contains(selectedFilm)) {
                statusLabel.setText("Film is already in your list.");
            } else {
                statusLabel.setText("");
            }

            // Add action listeners for both buttons
            likeButton.setOnAction(e -> {
                System.out.println("Liked");
                dislikeButton.setStyle("-fx-opacity: 0.5;"); // Make the dislike button semi-transparent
                likeButton.setStyle("-fx-opacity: 1;"); // Full opacity for the like button
                likeFilm();
            });

            dislikeButton.setOnAction(e -> {
                System.out.println("Disliked");
                likeButton.setStyle("-fx-opacity: 0.5;"); // Make the like button semi-transparent
                dislikeButton.setStyle("-fx-opacity: 1;"); // Full opacity for the dislike button
                dislikeFilm();
            });

        } else {
            // If no film is selected, handle appropriately (e.g., show an error)
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Film not found", "Unable to retrieve the selected film.");
        }
    }

    // This method updates the labels with the details of the selected film.
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
        updateAverageScore();

        // Check if the current profile has already liked or disliked the film
        Profile currentProfile = SessionData.getInstance().getCurrentProfile();
        if (currentProfile != null) {
            int reviewId = filmManager.getReviewId(selectedFilm.getFilmId(), currentProfile.getProfileId());
            if (reviewId != -1) {
            boolean isLiked = filmManager.isReviewLiked(reviewId);
            if (isLiked) {
                likeButton.setStyle("-fx-opacity: 1;");
                dislikeButton.setStyle("-fx-opacity: 0.5;");
            } else {
                likeButton.setStyle("-fx-opacity: 0.5;");
                dislikeButton.setStyle("-fx-opacity: 1;");
            }
            } else {
            // No review found, keep both buttons fully visible
            likeButton.setStyle("-fx-opacity: 1;");
            dislikeButton.setStyle("-fx-opacity: 1;");
            }
        }
    }

    // This method sets the visibility of the Rent and Stream buttons based on the logged-in customer's account type.
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

    @FXML
    private void handleSaveToList() {
        if (selectedFilm != null) {
            Profile currentProfile = SessionData.getInstance().getCurrentProfile();
            if (currentProfile != null) {
                // Check if the film is already in the saved list
                if (!SessionData.getInstance().getSavedFilms().contains(selectedFilm)) {
                    // Add film to session
                    SessionData.getInstance().addFilmToSavedList(selectedFilm);
                    System.out.println("Film added to SessionData saved films.");
    
                    // Add film to the database under the current profile
                    FilmManager.addFilmToMyList(currentProfile.getProfileId(), selectedFilm.getFilmId());
                    System.out.println("Film added to My List in the database.");
    
                    // Update status label and disable the save button
                    statusLabel.setText("Film added to your list.");
                    saveToListButton.setDisable(true);
                } else {
                    // Film is already in the list, hide or disable the button
                    statusLabel.setText("Film is already in your list.");
                    saveToListButton.setDisable(true);
                }
            } else {
                System.out.println("No profile selected, cannot save film to My List in the database.");
                statusLabel.setText("No profile selected.");
            }
        } else {
            System.out.println("No film selected to save.");
            statusLabel.setText("No film selected to save.");
        }
    }

    /**
     * Method to handle the Rent button click event.
     * It switches the scene to the Rent Film screen.
     */
    @FXML
    private void showRentWindow() {
        StageUtils.switchScene(
            (Stage) titleLabel.getScene().getWindow(), 
            "rentFilm", 
            "Streamify - Rent Film");

        System.out.println("Rent window for film: " + selectedFilm.getTitle());
    }

    /**
     * Method to add like for the selected film and update average score shown.
     */
    private void likeFilm() {
        int reviewId = filmManager.addLikeDislike(selectedFilm.getFilmId(), SessionData.getInstance().getCurrentProfile().getProfileId(), true);
        if (reviewId != -1) {
            updateAverageScore();
        } else {
            System.out.println("Error: Unable to like film.");
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Like Failed", "Unable to like the film. Please try again later.");
        }        
    }

    /**
     * Method to add dislike for the selected film and update average score shown.
     */
    private void dislikeFilm() {
        System.out.println("Disliking film: " + selectedFilm.getTitle());
        int reviewId = filmManager.addLikeDislike(selectedFilm.getFilmId(), SessionData.getInstance().getCurrentProfile().getProfileId(), false);
        
        // Debugging the returned review ID
        System.out.println("Returned reviewId: " + reviewId);
    
        if (reviewId != -1) {
            updateAverageScore();
        } else {
            System.out.println("Error: Unable to dislike film.");
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Dislike Failed", "Unable to dislike the film. Please try again later.");
        }
    }    

    private void updateAverageScore() {
        System.out.println("Updating average score for film: " + selectedFilm.getTitle());
        double avgScore = filmManager.getAverageReviewScore(selectedFilm.getFilmId());
        if (avgScore != 6.00) {
            avgScoreLabel.setText("Average score: " + avgScore);
        } else {
            System.out.println("Error: Unable to update average score.");
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Update Failed", "Unable to update the average score. Please try again later.");
        }
    }

    // TO DO
    @FXML
    private void tryToStream() {
        // Try to stream the selected film
        System.out.println("Trying to stream film: " + selectedFilm.getTitle());
    }

    // TO DO
    @FXML
    private void tryToOpenAllReviews() {
        System.out.println("Opening all reviews for film: " + selectedFilm.getTitle());
    }
}
