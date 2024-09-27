package com.oap2024team7.team7mediastreamingapp.services;

import java.util.List;
import java.util.ArrayList;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.models.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmManager {

    // Helper method to extract language data and create a Language object
    private Language getLanguageById(Connection conn, int languageId) throws SQLException {
        String query = "SELECT * FROM language WHERE language_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, languageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Language(rs.getInt("language_id"), rs.getString("name"));
            }
        }
        return null;
    }

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
        String getQuery = "SELECT * FROM film ORDER BY title";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                // Fetch the language object
                Language language = getLanguageById(conn, rs.getInt("language_id"));

                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", ""))
                );
                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Film> getFilmsSortedByYear() {
        String getQuery = "SELECT * FROM film ORDER BY release_year";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                // Fetch the language object
                Language language = getLanguageById(conn, rs.getInt("language_id"));

                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", ""))
                );
                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Film> getAllFilms(int offset, int limit) {
        String getQuery = "SELECT * FROM film LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                // Fetch the language object
                Language language = getLanguageById(conn, rs.getInt("language_id"));

                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", ""))
                );
                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Film getFilmById(int filmId) {
        String getQuery = "SELECT * FROM film WHERE film_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getQuery)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Fetch the language object
                Language language = getLanguageById(conn, rs.getInt("language_id"));

                return new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", ""))
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Using Integer instead of int to allow for null values
    public List<Film> filterFilms(Integer categoryId, Film.Rating rating, Integer maxLength, Integer startYear, Integer endYear, int offset, int limit) {
        StringBuilder filterQuery = new StringBuilder("SELECT f.* FROM film f JOIN film_category fc ON f.film_id = fc.film_id WHERE 1=1");

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

            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex++, offset);

            ResultSet rs = stmt.executeQuery();
            List<Film> filteredFilms = new ArrayList<>();
            while (rs.next()) {
                // Fetch the language object
                Language language = getLanguageById(conn, rs.getInt("language_id"));

                Film film = new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("release_year"),
                    language,  // Pass the Language object instead of language_id
                    rs.getInt("rental_duration"),
                    rs.getInt("length"),
                    Film.Rating.valueOf(rs.getString("rating").replace("-", ""))
                );
                filteredFilms.add(film);
            }
            return filteredFilms;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
