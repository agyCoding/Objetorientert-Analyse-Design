package com.oap2024team7.team7mediastreamingapp.services;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.oap2024team7.team7mediastreamingapp.models.Actor;
import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Language;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.models.Profile;
import com.oap2024team7.team7mediastreamingapp.models.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for the Film Manager.
 * This class is responsible for managing Film objects, providing functionality to add to, update, delete, and fetch films from the database.
 * @authors Agata (Agy) Olaussen (@agyCoding), Adnan Duric (@adovic)
 */

public class FilmManager {
    // private static Connection connection;

    /**
     * Helper method to map the database rating to the Film.Rating enum that's without the hyphen
     * @param rating
     * @return String representation of the rating
     */
    public static String mapRating(Film.Rating rating) {
        switch (rating) {
            case G:
                return "G";
            case PG:
                return "PG";
            case PG13: // Here we add the hyphen
                return "PG-13"; 
            case R:
                return "R";
            case NC17: // Here we add the hyphen
                return "NC-17";
            default:
                return null;
        }
    }

    /**
     * Inserts a new film into the database (film table and film_actor table).
     * @param film The film to insert
     * @return The ID of the newly inserted film, or -1 if insertion failed
     */
    public int addFilm(Film film) {
        String insertFilmQuery = "INSERT INTO film (title, description, release_year, language_id, rental_duration, rental_rate, length, rating, special_features) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertFilmActorQuery = "INSERT INTO film_actor (film_id, actor_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement filmStmt = conn.prepareStatement(insertFilmQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // Insert into film table
                filmStmt.setString(1, film.getTitle());
                filmStmt.setString(2, film.getDescription());
                filmStmt.setInt(3, film.getReleaseYear());
                filmStmt.setInt(4, film.getLanguage().getLanguageId());
                filmStmt.setInt(5, film.getRentalDuration());
                filmStmt.setDouble(6, film.getRentalRate());
                filmStmt.setInt(7, film.getLength());
                filmStmt.setString(8, mapRating(film.getRating()));
                filmStmt.setString(9, String.join(",", film.getSpecialFeatures()));
                
                int rowsAffected = filmStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get the generated film_id
                    ResultSet rs = filmStmt.getGeneratedKeys();
                    if (rs.next()) {
                        int filmId = rs.getInt(1);
                        
                        // Only try to insert actors if the list is not null and not empty
                        List<Actor> actors = film.getActors();
                        if (actors != null && !actors.isEmpty()) {
                            try (PreparedStatement actorStmt = conn.prepareStatement(insertFilmActorQuery)) {
                                for (Actor actor : actors) {
                                    actorStmt.setInt(1, filmId);
                                    actorStmt.setInt(2, actor.getActorId());
                                    actorStmt.executeUpdate();
                                }
                            }
                        }
                        conn.commit();
                        return filmId;
                    }
                }
                conn.rollback();
                return -1; 
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Updates a film in the database.
     * @param film The film object containing updated information.
     * @return boolean indicating if the film was updated successfully.
     */
    public boolean updateFilm(Film film) {
        String updateQuery = "UPDATE film SET title = ?, description = ?, release_year = ?, language_id = ?, rental_duration = ?, rental_rate = ?, length = ?, rating = ?, special_features = ? WHERE film_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setString(1, film.getTitle());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getReleaseYear());
            stmt.setInt(4, film.getLanguage().getLanguageId());
            stmt.setInt(5, film.getRentalDuration());
            stmt.setDouble(6, film.getRentalRate());
            stmt.setInt(7, film.getLength());
            stmt.setString(8, mapRating(film.getRating()));
            stmt.setString(9, String.join(",", film.getSpecialFeatures()));
            stmt.setInt(10, film.getFilmId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a film from the database,
     * taking under account all tables that have a foreign key relationship with the film.
     * @param film
     * @return boolean indicating if the film was deleted successfully
     */
    public boolean deleteFilm(Film film) {
        int filmId = film.getFilmId();
    
        // Step 1: Check if any inventory of the film is currently rented out
        String checkRentedQuery = "SELECT COUNT(*) FROM rental WHERE inventory_id IN (SELECT inventory_id FROM inventory WHERE film_id = ?)";
        int rentedCount = 0;
        
        try (Connection conn = DatabaseManager.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(checkRentedQuery)) {
             
            pstmt.setInt(1, filmId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rentedCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        // Step 2: If rented out, return false
        if (rentedCount > 0) {
            return false;
        }
    
        // Step 3: Proceed to delete the film and related entries
        String deleteFilmQuery = "DELETE FROM film WHERE film_id = ?";
        String deleteInventoryQuery = "DELETE FROM inventory WHERE film_id = ?";
        String deleteFilmCategoryQuery = "DELETE FROM film_category WHERE film_id = ?";
        String deleteFilmActorQuery = "DELETE FROM film_actor WHERE film_id = ?";
        String deleteFilmTextQuery = "DELETE FROM film_text WHERE film_id = ?";
        String deleteRentalQuery = "DELETE FROM rental WHERE inventory_id IN (SELECT inventory_id FROM inventory WHERE film_id = ?)";
    
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start a transaction
    
            try (PreparedStatement pstmt = conn.prepareStatement(deleteInventoryQuery)) {
                pstmt.setInt(1, filmId);
                pstmt.executeUpdate();
            }
    
            try (PreparedStatement pstmt = conn.prepareStatement(deleteFilmCategoryQuery)) {
                pstmt.setInt(1, filmId);
                pstmt.executeUpdate();
            }
    
            try (PreparedStatement pstmt = conn.prepareStatement(deleteFilmActorQuery)) {
                pstmt.setInt(1, filmId);
                pstmt.executeUpdate();
            }
    
            try (PreparedStatement pstmt = conn.prepareStatement(deleteFilmTextQuery)) {
                pstmt.setInt(1, filmId);
                pstmt.executeUpdate();
            }
    
            try (PreparedStatement pstmt = conn.prepareStatement(deleteRentalQuery)) {
                pstmt.setInt(1, filmId);
                pstmt.executeUpdate();
            }

            // Call this last to avoid foreign key constraint issues
            try (PreparedStatement pstmt = conn.prepareStatement(deleteFilmQuery)) {
                pstmt.setInt(1, filmId);
                pstmt.executeUpdate();
            }
             
            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseManager.getConnection()) {
                conn.rollback(); // Rollback in case of error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
        
        return true; // Film deleted successfully
    }

    /**
     * Fetches films from the database based on the offset and limit.
     * @param offset
     * @param limit
     * @return List of films
     */
    public List<Film> getAllFilms(int offset, int limit, int storeId, String sortBy) {
        String getQuery = "SELECT DISTINCT f.* FROM film f " +
        "JOIN inventory i ON f.film_id = i.film_id " +
        "WHERE i.store_id = ? " +
        "ORDER BY f." + sortBy + " " +
        "LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, storeId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();

            // Get the birth date of the current profile
            Profile currentProfile = SessionData.getInstance().getCurrentProfile();

            while (rs.next()) {
                // Fetch the language object
                LanguageManager languageManager = new LanguageManager();
                Language language = languageManager.getLanguageById(conn, rs.getInt("language_id"));

                // Fetch special features
                GeneralUtils utils = new GeneralUtils();
                Set<String> specialFeatures = utils.convertToSet(rs.getString("special_features"));                

                // Fetch the actors for this film
                List<Actor> actors = ActorManager.getInstance().getActorsForFilm(rs.getInt("film_id"));

                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", "")),
                    specialFeatures,
                    rs.getDouble("rental_rate"),
                    actors
                );
                // The method is reused between primary controller and admin page
                // Admins don't have profiles so we don't need to check if the film is watchable
                // Check which user is not null in the Session data and filter the films accordingly
                Staff currentStaff = SessionData.getInstance().getLoggedInStaff();
                if (currentStaff != null) {
                    films.add(film);
                } else if (ProfileManager.canWatchFilm(film, currentProfile)) {
                    films.add(film);
                }
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetches a detailed information about a film from the database based on the film ID.
     * @param filmId
     * @return Film object
     */
    public Film getFilmById(int filmId) {
        String getQuery = "SELECT * FROM film WHERE film_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Fetch the language object
                LanguageManager languageManager = new LanguageManager();
                Language language = languageManager.getLanguageById(conn, rs.getInt("language_id"));
    
                // Convert special_features from String to Set<String>
                String specialFeaturesString = rs.getString("special_features");
                Set<String> specialFeatures = new HashSet<>();
                if (specialFeaturesString != null && !specialFeaturesString.isEmpty()) {
                    specialFeatures.addAll(Arrays.asList(specialFeaturesString.split(",")));
                }
    
                // Fetch the actors for this film
                List<Actor> actors = ActorManager.getInstance().getActorsForFilm(rs.getInt("film_id"));
    
                return new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", "")),
                    specialFeatures,
                    rs.getDouble("rental_rate"),
                    actors
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    /**
     * Fetches all films from the database that match the search query (based on filters set by the user)
     * Using Integer instead of int to allow for null values
     * @param categoryId
     * @param rating
     * @param maxLength
     * @param startYear
     * @param endYear
     * @param offset
     * @param limit
     * @return List of films
     */
    public List<Film> filterFilms(Integer categoryId, Film.Rating rating, Integer maxLength, Integer startYear, Integer endYear, int offset, int limit, int storeId, String sortBy) {
        StringBuilder filterQuery = new StringBuilder("SELECT DISTINCT f.* FROM film f " +
        "JOIN film_category fc ON f.film_id = fc.film_id " +
        "JOIN inventory i ON f.film_id = i.film_id " +
        "WHERE 1=1");

        if (categoryId != null) {
            filterQuery.append(" AND fc.category_id = ?");
        }
        if (rating != null) {
            filterQuery.append(" AND f.rating = ?");
        }
        if (maxLength != null) {
            filterQuery.append(" AND f.length <= ?");
        }
        if (startYear != null) {
            filterQuery.append(" AND f.release_year >= ?");
        }
        if (endYear != null) {
            filterQuery.append(" AND f.release_year <= ?");
        }
        filterQuery.append(" AND i.store_id = ? ORDER BY f.").append(sortBy);
        filterQuery.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(filterQuery.toString())) {

            int paramIndex = 1;

            if (categoryId != null) {
                stmt.setInt(paramIndex++, categoryId);
            }
            if (rating != null) {
                stmt.setString(paramIndex++, rating.name());
            }
            if (maxLength != null) {
                stmt.setInt(paramIndex++, maxLength);
            }
            if (startYear != null) {
                stmt.setInt(paramIndex++, startYear);
            }
            if (endYear != null) {
                stmt.setInt(paramIndex++, endYear);
            }
            stmt.setInt(paramIndex++, storeId);
            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex++, offset);

            ResultSet rs = stmt.executeQuery();
            List<Film> filteredFilms = new ArrayList<>();

            Profile currentProfile = SessionData.getInstance().getCurrentProfile();

            while (rs.next()) {
                // Fetch the language object
                LanguageManager languageManager = new LanguageManager();
                Language language = languageManager.getLanguageById(conn, rs.getInt("language_id"));

                // Convert special_features from String to Set<String>
                String specialFeaturesString = rs.getString("special_features");
                Set<String> specialFeatures = new HashSet<>();
                if (specialFeaturesString != null && !specialFeaturesString.isEmpty()) {
                    specialFeatures.addAll(Arrays.asList(specialFeaturesString.split(",")));
                }

                // Fetch the actors for this film
                List<Actor> actors = ActorManager.getInstance().getActorsForFilm(rs.getInt("film_id"));

                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", "")),
                    specialFeatures,
                    rs.getDouble("rental_rate"),
                    actors
                );

                // The method is reused between primary controller and admin page
                // Admins don't have profiles so we don't need to check if the film is watchable
                // Check which user is not null in the Session data and filter the films accordingly
                Staff currentStaff = SessionData.getInstance().getLoggedInStaff();
                if (currentStaff != null) {
                    filteredFilms.add(film);
                } else if (ProfileManager.canWatchFilm(film, currentProfile)) {
                    filteredFilms.add(film);
                }
            }
            return filteredFilms;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // METHODS FOR MY LIST FUNCTIONALITY
    /**
     * Adds a film to the My List table for a given profile.
     * @param profileId The ID of the profile.
     * @param filmId The ID of the film to add.
     */
    public static void addFilmToMyList(int profileId, int filmId) {
        String insertQuery = "INSERT IGNORE INTO my_list (profile_id, film_id) VALUES (?, ?)";
    
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            conn.setAutoCommit(false); // Start a transaction
    
            stmt.setInt(1, profileId);
            stmt.setInt(2, filmId);
            stmt.executeUpdate();
    
            conn.commit(); // Commit the transaction
    
            System.out.println("Film added to My List.");
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
        }
    }
    

    /**
     * Removes a film from the My List table for a given profile.
     * @param profileId The ID of the profile.
     * @param filmId The ID of the film to remove.
     */
    public static void removeFilmFromMyList(int profileId, int filmId) {
        String deleteQuery = "DELETE FROM my_list WHERE profile_id = ? AND film_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            conn.setAutoCommit(false); // Start a transaction

            stmt.setInt(1, profileId);
            stmt.setInt(2, filmId);
            stmt.executeUpdate();

            conn.commit(); // Commit the transaction

            System.out.println("Film removed from My List.");
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
        }
    }

    /**
     * Retrieves the list of films from the My List table for a given profile.
     * @param profileId The ID of the profile.
     * @return A list of films that are in the profile's My List.
     */
    public static List<Film> getFilmsFromMyList(int profileId) {
        List<Film> films = new ArrayList<>();
        String selectQuery = "SELECT ml.film_id " +
            "FROM my_list ml " +
            "WHERE ml.profile_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int filmId = rs.getInt("film_id");

                FilmManager filmManager = new FilmManager();
                Film film = filmManager.getFilmById(filmId);
                if (film != null) {
                    films.add(film);
                }
            }
            System.out.println("Number of films retrieved for profile ID " + profileId + ": " + films.size());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid rating value encountered.");
            e.printStackTrace();
        }
        return films;
    }

    // METHODS FOR LIKES, DISLIKES AND REVIEWS

    /**
     * Retrieves the review ID for a given film and profile.
     * @param filmId
     * @param profileId
     * @return The review ID, or -1 if the review does not exist.
     */
    public int getReviewId(int filmId, int profileId) {
        String selectQuery = "SELECT review_id FROM film_review WHERE film_id = ? AND profile_id = ?";
        int reviewId = -1;

        System.out.println("Film ID: " + filmId + ", Profile ID: " + profileId); // Debugging

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setInt(1, filmId);
            stmt.setInt(2, profileId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                reviewId = rs.getInt("review_id");
            }
            return reviewId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Checks if a review is liked based on the review ID.
     * @param reviewId The ID of the review.
     * @return boolean indicating if the review is liked.
     */
    public boolean isReviewLiked(int reviewId) {
        String selectQuery = "SELECT liked FROM film_review WHERE review_id = ?";
        
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
     * @param filmId The ID of the film.
     * @param profileId The ID of the profile.
     * @param liked Boolean indicating if the review is liked (true) or disliked (false).
     * @return The review ID if successful, or -1 if there was an error.
     */
    public int addLikeDislike(int filmId, int profileId, boolean liked) {
        String insertQuery = "INSERT INTO film_review (film_id, profile_id, liked) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE film_review SET liked = ? WHERE film_id = ? AND profile_id = ?";

        // Retrieve the existing review ID
        int reviewId = getReviewId(filmId, profileId);

        // If review exists, update it
        if (reviewId != -1) {
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
     * @param filmId The ID of the film.
     * @param profileId The ID of the profile.
     * @param review The review text.
     * @return id of the review and -1 if the review does not exist
     */
    public int addReview(int filmId, int profileId, String review) {
        String insertQuery = "INSERT INTO film_review (film_id, profile_id, review) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE film_review SET review = ? WHERE film_id = ? AND profile_id = ?";
        String selectQuery = "SELECT review_id FROM film_review WHERE film_id = ? AND profile_id = ?";
        int reviewId = -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            conn.setAutoCommit(false); // Start a transaction

            selectStmt.setInt(1, filmId);
            selectStmt.setInt(2, profileId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                reviewId = rs.getInt("review_id");
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, review);
                    updateStmt.setInt(2, filmId);
                    updateStmt.setInt(3, profileId);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setInt(1, filmId);
                    insertStmt.setInt(2, profileId);
                    insertStmt.setString(3, review);
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

    
    // METHODS FOR HIGHER-LEVEL LOGIC RELATED TO DISPLAYING FILMS

    /**
     * Helper method for getting a list films based on filters, storeId and order, or all films if no filters are applied.
     */
    public List<Film> loadFilms(
        Integer categoryId,
        Film.Rating rating,
        Integer maxLength,
        Integer startYear,
        Integer endYear,
        int offset,
        int limit,
        int storeId,
        String sortBy) {
            List<Film> films;

            // Determine if filters are applied and set sorting order
            boolean hasFilters = categoryId != null || rating != null || maxLength != null || startYear != null || endYear != null;
            
            if (hasFilters) {
                films = filterFilms(categoryId, rating, maxLength, startYear, endYear, offset, limit, storeId, sortBy);
            } else {
                films = getAllFilms(offset, limit, storeId, sortBy);
            }
            
            return films;
    }

}
