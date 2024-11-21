package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ReviewManager {
    /**
     * Retrieves the Review object for a given film and profile.
     * @param filmId The ID of the film.
     * @param profileId The ID of the profile.
     * @return The Review object, or null if the review does not exist.
     */
    public Review getReview(int filmId, int profileId) {
        String selectQuery = "SELECT review_id, film_id, profile_id, review, liked, review_date FROM film_review WHERE film_id = ? AND profile_id = ?";
        Review review = null;

        System.out.println("Film ID: " + filmId + ", Profile ID: " + profileId); // Debugging

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, filmId);
            stmt.setInt(2, profileId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                review = new Review(
                    rs.getInt("review_id"),
                    rs.getInt("film_id"),
                    rs.getInt("profile_id"),
                    rs.getBoolean("liked"),
                    rs.getString("review"),
                    rs.getTimestamp("review_date").toLocalDateTime().toLocalDate()
                );
            }
            return review;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if a review is liked based on the review object.
     * @param review The Review object containing the review ID.
     * @return boolean indicating if the review is liked.
     */
    public boolean isReviewLiked(Review review) {
        String selectQuery = "SELECT liked FROM film_review WHERE review_id = ?";
        int reviewId = review.getReviewId();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, reviewId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("liked");
            } else {
                return false; // Review not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a like or dislike to a film review.
     * @param review The Review object containing filmId, profileId, and liked status.
     * @return The review ID if successful, or -1 if there was an error.
     */
    public int addLikeDislike(Review review) {
        String insertQuery = "INSERT INTO film_review (film_id, profile_id, liked) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE film_review SET liked = ? WHERE film_id = ? AND profile_id = ?";

        int filmId = review.getFilmId();
        int profileId = review.getProfileId();
        boolean liked = review.isLiked();

        // Retrieve the existing review
        Review existingReview = getReview(filmId, profileId);
        int reviewId = -1;

        // If review exists, update it
        if (existingReview != null) {
            // If review exists, get its ID
            reviewId = existingReview.getReviewId();

            try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

                updateStmt.setBoolean(1, liked);
                updateStmt.setInt(2, filmId);
                updateStmt.setInt(3, profileId);
                updateStmt.executeUpdate();
                System.out.println("Review updated successfully.");

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            // If review does not exist, insert a new review
            try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

                insertStmt.setInt(1, filmId);
                insertStmt.setInt(2, profileId);
                insertStmt.setBoolean(3, liked);
                insertStmt.executeUpdate();

                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reviewId = generatedKeys.getInt(1);
                    System.out.println("New review added with ID: " + reviewId);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }

        return reviewId;
    }

    /**
     * Retrieves all review records for a specific filmId and calculates the average score.
     * Each like is assigned a value of 5 and each dislike is assigned a value of 0.
     * @param filmId The ID of the film.
     * @return The average score with two decimals, or 6.00 if there was an error.
     */
    public double getAverageReviewScore(int filmId) {
        String selectQuery = "SELECT liked FROM film_review WHERE film_id = ?";
        int totalScore = 0;
        int reviewCount = 0;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                boolean liked = rs.getBoolean("liked");
                totalScore += liked ? 5 : 0;
                reviewCount++;
            }

            if (reviewCount == 0) {
                return 0.00;
            }

            double averageScore = (double) totalScore / reviewCount;
            return Math.round(averageScore * 100.0) / 100.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 6.00; // Return 6.00 if there was an error (unattainable score)
        }
    }

    /**
     * Adds a review to a film.
     * @param review The Review object containing filmId, profileId, and review text.
     * @return id of the review and -1 if the review does not exist
     */
    public int addReview(Review review) {
        String insertQuery = "INSERT INTO film_review (film_id, profile_id, review) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE film_review SET review = ? WHERE film_id = ? AND profile_id = ?";
        String selectQuery = "SELECT review_id FROM film_review WHERE film_id = ? AND profile_id = ?";
        int reviewId = -1;

        int filmId = review.getFilmId();
        int profileId = review.getProfileId();
        String reviewText = review.getReview();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            conn.setAutoCommit(false); // Start a transaction

            selectStmt.setInt(1, filmId);
            selectStmt.setInt(2, profileId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                reviewId = rs.getInt("review_id");
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, reviewText);
                    updateStmt.setInt(2, filmId);
                    updateStmt.setInt(3, profileId);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setInt(1, filmId);
                    insertStmt.setInt(2, profileId);
                    insertStmt.setString(3, reviewText);
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        reviewId = generatedKeys.getInt(1);
                    }
                }
            }

            conn.commit(); // Commit the transaction
            return reviewId;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                Connection conn = DatabaseManager.getConnection();
                if (conn != null) {
                    conn.rollback(); // Rollback if there's an issue
                    System.err.println("Transaction rolled back due to an error.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return -1;
        }
    }

    /**
     * Retrieves all reviews for a specific filmId from the film_review table.
     * @param filmId The ID of the film.
     * @return A list of Review objects.
     */
    public List<Review> getReviewsByFilmId(int filmId) {
        String selectQuery = "SELECT review_id, film_id, profile_id, review, liked, review_date FROM film_review WHERE film_id = ?";
        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("review_id"),
                    rs.getInt("film_id"),
                    rs.getInt("profile_id"),
                    rs.getBoolean("liked"),
                    rs.getString("review"),
                    rs.getTimestamp("review_date").toLocalDateTime().toLocalDate()
                );
                if (review.getReview() != null) {
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}