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
 * This class is responsible for managing Film objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class FilmManager {
    private static Connection connection;

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
     * Fetches all films from the database, sorted by title.
     * @return List of all films
     */
    public List<Film> getFilmsSortedByTitle(int storeId) {
        String getQuery = "SELECT DISTINCT f.* FROM film f " +
                          "JOIN inventory i ON f.film_id = i.film_id " +
                          "WHERE i.store_id = ? " +
                          "ORDER BY f.title";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, storeId);  // Filter by store_id
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();

            Profile currentProfile = SessionData.getInstance().getCurrentProfile();

            while (rs.next()) {
                LanguageManager languageManager = new LanguageManager();
                Language language = languageManager.getLanguageById(conn, rs.getInt("language_id"));
                GeneralUtils utils = new GeneralUtils();
                Set<String> specialFeatures = utils.convertToSet(rs.getString("special_features"));
                List<Actor> actors = ActorManager.getInstance().getActorsForFilm(rs.getInt("film_id"));
    
                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,
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
     * Fetches all films from the database, sorted by release year.
     * @return List of all films
     */
    public List<Film> getFilmsSortedByYear(int storeId) {
        String getQuery = "SELECT DISTINCT f.* FROM film f " +
                      "JOIN inventory i ON f.film_id = i.film_id " +
                      "WHERE i.store_id = ? " +
                      "ORDER BY f.release_year";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();

            Profile currentProfile = SessionData.getInstance().getCurrentProfile();

            while (rs.next()) {
                // Fetch the language object
                LanguageManager languageManager = new LanguageManager();
                Language language = languageManager.getLanguageById(conn, rs.getInt("language_id"));

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
     * Fetches films from the database based on the offset and limit.
     * @param offset
     * @param limit
     * @return List of films
     */
    public List<Film> getAllFilms(int offset, int limit, int storeId) {
        String getQuery = "SELECT DISTINCT f.* FROM film f " +
        "JOIN inventory i ON f.film_id = i.film_id " +
        "WHERE i.store_id = ? " +
        "ORDER BY f.title " +
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
    public List<Film> filterFilms(Integer categoryId, Film.Rating rating, Integer maxLength, Integer startYear, Integer endYear, int offset, int limit, int storeId) {
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
        filterQuery.append(" AND i.store_id = ?");
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
    
}
