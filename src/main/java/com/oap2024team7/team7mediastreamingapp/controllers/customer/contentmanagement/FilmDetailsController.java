package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Customer;
import com.oap2024team7.team7mediastreamingapp.models.Customer.AccountType;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.services.ActorManager;
import com.oap2024team7.team7mediastreamingapp.services.FilmManager;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.services.RentalManager;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.services.ReviewManager;
import com.oap2024team7.team7mediastreamingapp.models.Review;
import com.oap2024team7.team7mediastreamingapp.services.InventoryManager;
import com.oap2024team7.team7mediastreamingapp.models.Inventory;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;

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
    @FXML
    private Label reviewsLabel;
    @FXML
    private Text reviewsText;
    @FXML
    private Hyperlink allReviewsHL;

    private Film selectedFilm;
    private Stage stage;
    private ReviewManager reviewManager;
    private InventoryManager inventoryManager;
    private RentalManager rentalManager = new RentalManager();
   
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
        reviewManager = new ReviewManager();
        inventoryManager = new InventoryManager();
    
        if (selectedFilm != null) {
            // Now that the film is set, update the labels with the film's details
            updateFilmDetails();
    
            // Update the status label based on whether the film is already in the list
            if (SessionData.getInstance().getSavedFilms().contains(selectedFilm)) {
                statusLabel.setText("Film is already in your list.");
            } else {
                statusLabel.setText("");
            }

            // Set Rent/Stream button visibility based on account type
            setRentStreamButtonVisibility();

            // Set visibility of rating and review buttons based on film attributes
            setRatingAndReviewButtonVisibility();

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
            Review review = reviewManager.getReview(selectedFilm.getFilmId(), currentProfile.getProfileId());
            if (review != null) {
            boolean isLiked = reviewManager.isReviewLiked(review);
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

        displayAllReviews();
    }

    /**
     * Method to set the visibility of the Rent and Stream buttons based on the customer's account type,
     * film attributes and inventory availability.
     * It provides feedback to the customer if they already have an active rental for the selected film and
     * if there is no available inventory for the selected film within the next 24 hours.
     * 
     */
    private void setRentStreamButtonVisibility() {
        // Retrieve the logged-in customer
        Customer loggedInCustomer = SessionData.getInstance().getLoggedInCustomer();

        if (loggedInCustomer != null) {
            AccountType accountType = loggedInCustomer.getAccountType();
            LocalDateTime now = LocalDateTime.now();

            // Check if the customer already has an active rental for the selected film
            boolean hasActiveRental = rentalManager.customerHasActiveRental(
                loggedInCustomer.getCustomerId(), selectedFilm.getFilmId(), now, now.plusDays(1)
            );

            if (hasActiveRental) {
                // Customer already has an active rental; disable both buttons and update status label
                rentButton.setDisable(true);
                streamButton.setDisable(true);
                statusLabel.setText("You already have an active rental for this film.");
            } else {
                // Customer does not have an active rental; proceed with checking general availability
                List<Inventory> availableInventories = inventoryManager.checkForAvailableInventory(
                    selectedFilm.getFilmId(), loggedInCustomer.getStoreId(), now, now.plusDays(1)
                );

                if (availableInventories.isEmpty()) {
                    // No inventory available, disable both buttons
                    rentButton.setDisable(true);
                    streamButton.setDisable(true);

                    // Get the next availability date
                    LocalDateTime nextAvailableDate = inventoryManager.getNextAvailableDate(selectedFilm.getFilmId(), loggedInCustomer.getStoreId());

                    if (nextAvailableDate != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy 'at' h:mm a");
                        statusLabel.setText("Next availability for renting/streaming: " + nextAvailableDate.format(formatter));
                        statusLabel.setStyle("-fx-text-fill: red;");
                    } else {
                        statusLabel.setText("No inventory available.");
                        statusLabel.setStyle("-fx-text-fill: red;");
                    }
                } else {
                    // Inventory available, enable both buttons and set visibility based on account type and film attributes
                    if (accountType == AccountType.FREE) {
                        if (selectedFilm.isStreamable()) {
                            // Show Stream button and hide Rent button if the film is streamable
                            rentButton.setVisible(false);
                            streamButton.setVisible(true);
                        } else {
                            // Show Rent button and hide Stream button if the film is not streamable
                            rentButton.setVisible(true);
                            streamButton.setVisible(false);
                        }
                    } else if (accountType == AccountType.PREMIUM) {
                        // Always show Stream button and hide Rent button for PREMIUM users
                        rentButton.setVisible(false);
                        streamButton.setVisible(true);
                    }
                    statusLabel.setText("");
                }
            }
        }
    }

    /**
     * Method to set the visibility of the rating and review buttons based on the film's attributes (set by admin).
     */
    private void setRatingAndReviewButtonVisibility() {
        // Hide the like/dislike buttons and review hyperlink if the film is not ratable or reviewable
        if (selectedFilm != null) {
            if (!selectedFilm.isRatable()) {
                // Hide rating buttons if film isn't supposed to be rated
                likeButton.setVisible(false);
                dislikeButton.setVisible(false);
                avgScoreLabel.setVisible(false);
            } else {
                likeButton.setVisible(true);
                dislikeButton.setVisible(true);
                avgScoreLabel.setVisible(true);
            }

            if (!selectedFilm.isReviewable()) {
                reviewsLabel.setVisible(false); // Hide the label for reviews
                reviewsText.setVisible(false); // Hide review text area if not reviewable
                allReviewsHL.setVisible(false); // Hide the hyperlink to view all reviews and add a review
            } else {
                reviewsLabel.setVisible(true);
                reviewsText.setVisible(true);
                allReviewsHL.setVisible(true);
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
        Review newLike = new Review(selectedFilm.getFilmId(), SessionData.getInstance().getCurrentProfile().getProfileId(), true);
        int reviewId = reviewManager.addLikeDislike(newLike);
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
        Review newDislike = new Review(selectedFilm.getFilmId(), SessionData.getInstance().getCurrentProfile().getProfileId(), false);
        int reviewId = reviewManager.addLikeDislike(newDislike);
    
        if (reviewId != -1) {
            updateAverageScore();
        } else {
            System.out.println("Error: Unable to dislike film.");
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Dislike Failed", "Unable to dislike the film. Please try again later.");
        }
    }    

    /**
     * Method to update and show the average score for the selected film.
     * This method is called after a like or dislike is added, and at the start of the controller.
     */
    private void updateAverageScore() {
        System.out.println("Updating average score for film: " + selectedFilm.getTitle());
        double avgScore = reviewManager.getAverageReviewScore(selectedFilm.getFilmId());
        if (avgScore != 6.00) {
            avgScoreLabel.setText("Average score: " + avgScore);
        } else {
            System.out.println("Error: Unable to update average score.");
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Update Failed", "Unable to update the average score. Please try again later.");
        }
    }

    /**
     * Display all reviews for the selected film.
     */
    @FXML
    private void displayAllReviews() {
        int filmId = selectedFilm.getFilmId();

        // Get all reviews for the selected film from the database
        List<Review> reviews = reviewManager.getReviewsByFilmId(filmId);

        // Clear the existing text
        reviewsText.setText("");

        // Format and display each review
        StringBuilder reviewsTextBuilder = new StringBuilder();
        for (Review review : reviews) {
            int profileId = review.getProfileId();
            String profileName = ProfileManager.getProfileById(profileId).getProfileName();
            String reviewDate = review.getReviewDate().toString();
            String reviewText = review.getReview();

            reviewsTextBuilder.append(profileName)
                              .append(" (")
                              .append(reviewDate)
                              .append("): ")
                              .append(reviewText)
                              .append("\n");
        }

        reviewsText.setText(reviewsTextBuilder.toString());
    }

    // TO DO
    @FXML
    private void tryToStream() {
        // Try to stream the selected film
        System.out.println("Trying to stream film: " + selectedFilm.getTitle());
    }

    @FXML
    private void tryToOpenAllReviews() {
        System.out.println("Opening all reviews for film: " + selectedFilm.getTitle());
        StageUtils.switchScene(
            (Stage) titleLabel.getScene().getWindow(), 
            "review", 
            "Streamify - All Reviews");
    }
}
