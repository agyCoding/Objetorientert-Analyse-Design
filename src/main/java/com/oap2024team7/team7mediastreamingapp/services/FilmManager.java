package com.oap2024team7.team7mediastreamingapp.services;

import java.util.List;
import java.util.ArrayList;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmManager {

    // Helper method to map the database rating to the Film.Rating enum that's without the hyphen
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

    public List<Film> getFilmsSortedByTitle() {
        // Get all films from the database
        String getQuery = "SELECT * FROM film ORDER BY title";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film(rs.getInt("film_id"), rs.getString("title"), rs.getString("description"), rs.getInt("release_year"), rs.getInt("language_id"), rs.getInt("rental_duration"), rs.getInt("length"), Film.Rating.valueOf(rs.getString("rating").replace("-", "")));
                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Film> getFilmsSortedByYear() {
        // Get all films from the database
        String getQuery = "SELECT * FROM film ORDER BY release_year";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film(rs.getInt("film_id"), rs.getString("title"), rs.getString("description"), rs.getInt("release_year"), rs.getInt("language_id"), rs.getInt("rental_duration"), rs.getInt("length"), Film.Rating.valueOf(rs.getString("rating").replace("-", "")));
                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Film> getAllFilms(int offset, int limit) {
        // Get all films from the database
        String getQuery = "SELECT * FROM film LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film(rs.getInt("film_id"), rs.getString("title"), rs.getString("description"), rs.getInt("release_year"), rs.getInt("language_id"), rs.getInt("rental_duration"), rs.getInt("length"), Film.Rating.valueOf(rs.getString("rating").replace("-", "")));
                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Film getFilmById(int filmId) {
        // Get film from the database
        String getQuery = "SELECT * FROM film WHERE film_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Film(rs.getInt("film_id"), rs.getString("title"), rs.getString("description"), rs.getInt("release_year"), rs.getInt("language_id"), rs.getInt("rental_duration"), rs.getInt("length"), Film.Rating.valueOf(rs.getString("rating").replace("-", "")));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Using Integer instead of int to allow for null values
    public List<Film> filterFilms(Integer categoryId, Film.Rating rating, Integer maxLength, Integer startYear, Integer endYear) {
    
        StringBuilder filterQuery = new StringBuilder("SELECT f.* FROM film f ");
        filterQuery.append("JOIN film_category fc ON f.film_id = fc.film_id ");
        filterQuery.append("WHERE 1=1 "); // This makes it easier to append conditions

        List<Object> params = new ArrayList<>();

        // Check for category filter
        if (categoryId != null) {
            filterQuery.append("AND fc.category_id = ? ");
            params.add(categoryId);
        }

        // Check for rating filter
        if (rating != null) {
            filterQuery.append(" AND f.rating = ?");
            params.add(mapRating(rating)); // Use the mapping function
        }

        // Check for max length filter
        if (maxLength != null) {
            filterQuery.append("AND f.length <= ? ");
            params.add(maxLength);
        }

        // Check for release year filters
        if (startYear != null && endYear != null) {
            filterQuery.append("AND f.release_year BETWEEN ? AND ? ");
            params.add(startYear);
            params.add(endYear);
        }
        
        // Check for release year filters when only one of the years is provided
        if (startYear != null && endYear == null) {
            filterQuery.append("AND f.release_year >= ? ");
            params.add(startYear);
        } else if (startYear == null && endYear != null) {
            filterQuery.append("AND f.release_year <= ? ");
            params.add(endYear);
        }

        // FOR DEBUGGING
        System.out.println("Filter Query: " + filterQuery.toString());
        System.out.println("Parameters: " + params);

        // Construct the PreparedStatement
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(filterQuery.toString())) {
            
            // Set the parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Film> filteredFilms = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film(rs.getInt("film_id"), rs.getString("title"), rs.getString("description"), rs.getInt("release_year"), rs.getInt("language_id"), rs.getInt("rental_duration"), rs.getInt("length"), Film.Rating.valueOf(rs.getString("rating").replace("-", "")));
                filteredFilms.add(film);
            }
            return filteredFilms;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }  
    }
}
