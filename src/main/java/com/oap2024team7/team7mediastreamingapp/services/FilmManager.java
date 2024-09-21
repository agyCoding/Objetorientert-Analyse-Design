package com.oap2024team7.team7mediastreamingapp.services;

import java.util.List;
import java.util.ArrayList;

import com.oap2024team7.team7mediastreamingapp.models.Film;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmManager {

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
}
