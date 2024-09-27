package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

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
}
