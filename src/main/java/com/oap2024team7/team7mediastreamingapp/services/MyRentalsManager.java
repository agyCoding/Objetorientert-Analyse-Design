package com.oap2024team7.team7mediastreamingapp.services;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;

/**
 * MyRentalsManager class
 * This class is responsible for managing the user's rented films.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class MyRentalsManager {

    private int customerId = SessionData.getInstance().getLoggedInCustomer().getCustomerId();
    FilmManager filmManager = new FilmManager();

    public List<Film> loadRentedFilmsFromDatabase() {
        List<Film> films = new ArrayList<>();
        String query = "SELECT f.* FROM rental r " +
                       "JOIN inventory i ON r.inventory_id = i.inventory_id " +
                       "JOIN film f ON i.film_id = f.film_id " +
                       "WHERE r.customer_id = ? AND r.return_date > CURRENT_DATE";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int filmId = resultSet.getInt("film_id");

                Film film = filmManager.getFilmById(filmId);
                if (film != null) {
                    // Only add the film to the list if the current profile can watch it
                    if (ProfileManager.canWatchFilm(film, SessionData.getInstance().getCurrentProfile())) {
                        films.add(film);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

}
