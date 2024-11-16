package com.oap2024team7.team7mediastreamingapp.models;

import java.time.LocalDate;

/**
 * Class for the Review object.
 * This class is responsible for creating and managing Review objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class Review {
    private int reviewId;
    private int filmId;
    private int profileId;
    private boolean liked;
    private String review;
    private LocalDate reviewDate;

    // Full constructor for getting review from the DB
    public Review(int reviewId, int filmId, int profileId, boolean liked, String review, LocalDate reviewDate) {
        this.reviewId = reviewId;
        this.filmId = filmId;
        this.profileId = profileId;
        this.liked = liked;
        this.review = review;
        this.reviewDate = reviewDate;
    }

    // Simple constructor for adding a new review
    public Review(int filmId, int profileId, String review) {
        this.filmId = filmId;
        this.profileId = profileId;
        this.review = review;
        this.reviewDate = LocalDate.now();
    }

    // Simple contructor for adding a new like/dislike
    public Review(int filmId, int profileId, boolean liked) {
        this.filmId = filmId;
        this.profileId = profileId;
        this.liked = liked;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
}