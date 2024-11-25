package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.util.List;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Review;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.services.ProfileManager;
import com.oap2024team7.team7mediastreamingapp.services.ReviewManager;

/**
 * Controller class for the Review screen.
 * This class is responsible for handling user input and displaying data on the Review screen.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class ReviewController {
    @FXML
    private Label filmTitleLabel;
    @FXML
    private TextArea newReviewTA;
    @FXML
    private Text allReviewsText;
    @FXML
    private Button addReviewButton;

    private Film selectedFilm;
    private ReviewManager reviewManager;

    @FXML
    private void initialize() {
        // Get the selected film from the session data
        selectedFilm = SessionData.getInstance().getSelectedFilm();
        filmTitleLabel.setText(selectedFilm.getTitle() + " (" + selectedFilm.getReleaseYear() + ")");
        reviewManager = new ReviewManager();

        // Check if the current profile has already reviewed the selected film
        Review existingReview = hasCurrentProfileReviewed();
        if (existingReview != null) {
            newReviewTA.setText(existingReview.getReview());
            newReviewTA.setStyle("-fx-opacity: 0.5;");
            addReviewButton.setText("Edit your review");
        }

        displayAllReviews();
    }

    /**
     * Check if the current profile has already reviewed the selected film.
     * @return the Review object if the current profile has already reviewed the film, null otherwise
     */
    private Review hasCurrentProfileReviewed() {
        int filmId = selectedFilm.getFilmId();
        int profileId = SessionData.getInstance().getCurrentProfile().getProfileId();

        // Get all reviews for the selected film from the database
        List<Review> reviews = reviewManager.getReviewsByFilmId(filmId);

        // Check if the current profile has already reviewed the film
        for (Review review : reviews) {
            if (review.getProfileId() == profileId) {
                return review;
            }
        }
        return null;
    }

    /**
     * Add a new review for the selected film.
     * This method is called when the user clicks the 'Add Review' button.
     */
    @FXML
    private void addYourReview() {
        String newReview = newReviewTA.getText();
        newReview = newReview.trim().replaceAll("\\s+", " ");

        int filmId = selectedFilm.getFilmId();
        int profileId = SessionData.getInstance().getCurrentProfile().getProfileId();

        // Add the review to the database
        Review review = new Review(filmId, profileId, newReview);
        int reviewId = reviewManager.addReview(review);
        if (reviewId != -1) {
            // Refresh the reviews
            displayAllReviews();
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
        allReviewsText.setText("");

        // Format and display each review
        StringBuilder reviewsText = new StringBuilder();
        for (Review review : reviews) {
            int profileId = review.getProfileId();
            String profileName = ProfileManager.getProfileById(profileId).getProfileName();
            String reviewDate = review.getReviewDate().toString();
            String reviewText = review.getReview();

            reviewsText.append(profileName)
                       .append(" (")
                       .append(reviewDate)
                       .append("): ")
                       .append(reviewText)
                       .append("\n");
        }

        allReviewsText.setText(reviewsText.toString());
    }
}