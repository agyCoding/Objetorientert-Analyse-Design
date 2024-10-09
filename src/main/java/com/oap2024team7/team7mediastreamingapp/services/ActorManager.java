package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the Actor Manager.
 * This class is responsible for managing Actor objects.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class ActorManager {
    // Singleton instance
    private static ActorManager instance;

    // Private constructor to prevent instantiation
    private ActorManager() {
    }

    // Get the singleton instance
    public static ActorManager getInstance() {
        if (instance == null) {
            instance = new ActorManager();
        }
        return instance;
    }

    /**
     * Get all actors from the database for a specific film.
     * @param filmId The ID of the film
     * @return List of all actors
     */
    public List<Actor> getActorsForFilm(int filmId) {
        List<Actor> actors = new ArrayList<>();
        String query = "SELECT a.actor_id, a.first_name, a.last_name " +
                       "FROM actor a " +
                       "JOIN film_actor fa ON a.actor_id = fa.actor_id " +
                       "WHERE fa.film_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, filmId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Actor actor = new Actor(
                    rs.getInt("actor_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
                );
                actors.add(actor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }
    
    /**
     * Set actors for a specific film.
     * @param actors List of actors to be set for the film
     * @param filmId The ID of the film
     * @return boolean indicating success or failure
     */
    public boolean setActorsForFilm(List<Actor> actors, int filmId) {
        String deleteQuery = "DELETE FROM film_actor WHERE film_id = ?";
        String insertQuery = "INSERT INTO film_actor (film_id, actor_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Delete existing actors for the film
            deleteStmt.setInt(1, filmId);
            deleteStmt.executeUpdate();
            
            // Insert new actors for the film
            for (Actor actor : actors) {
                insertStmt.setInt(1, filmId);
                insertStmt.setInt(2, actor.getActorId());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
            
            // Commit transaction
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
