// Last Modified: 30.09.2024
package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import com.oap2024team7.team7mediastreamingapp.models.Film;
import com.oap2024team7.team7mediastreamingapp.utils.GeneralUtils;
import com.oap2024team7.team7mediastreamingapp.utils.SessionData;
import com.oap2024team7.team7mediastreamingapp.utils.StageUtils;
import com.oap2024team7.team7mediastreamingapp.services.InventoryManager;
import com.oap2024team7.team7mediastreamingapp.models.Inventory;
import com.oap2024team7.team7mediastreamingapp.services.DiscountManager;
import com.oap2024team7.team7mediastreamingapp.models.Discount;
import com.oap2024team7.team7mediastreamingapp.models.Rental;
import com.oap2024team7.team7mediastreamingapp.models.Payment;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for the rent film view
 * This class is responsible for handling the user input and handling the renting of the selected film
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class RentFilmController {
    @FXML
    private Label selectedFilmLabel;
    @FXML
    private Label maxRentalLengthLabel;
    @FXML
    private Label rentalRateLabel;
    @FXML
    private TextField rentalDaysTF;
    @FXML   
    private Label totalCostLabel;

    private Film selectedFilm;
    private PauseTransition pause;
    private double totalCost;
    private LocalDateTime rentalStartDate = LocalDateTime.now();
    private LocalDateTime rentalEndDate;

    private static final String ERROR_TITLE = "Error";
    private static final String ERROR_MESSAGE = "An error occurred while trying to rent the film.";
    private static final String NO_INVENTORY_MESSAGE = "No available inventory for the selected film within this period of time.";
    private static final String INVALID_INPUT_MESSAGE = "Please enter a valid number of days.";

    private double rentalRate;

    /**
     * This method initializes the view with the selected film's details
     * and sets up the pause transition for updating the total cost
    */
    @FXML
    private void initialize() {
        // Get the selected film from the session data
        selectedFilm = SessionData.getInstance().getSelectedFilm();
        rentalDaysTF.setText("0");

        // Display the selected film's details
        String filmString = selectedFilm.getTitle() + " (" + selectedFilm.getReleaseYear() + ")";
        selectedFilmLabel.setText(filmString);
        maxRentalLengthLabel.setText("Max rental duration: " + String.valueOf(selectedFilm.getRentalDuration()));

        // Get information about the current rental rate and if there's a discount
        rentalRate = selectedFilm.getRentalRate();
        DiscountManager discountManager = new DiscountManager();
        Discount discount = discountManager.getActiveDiscount(selectedFilm.getFilmId());

        // Change rental rate to discounted rate if there's an active discount
        if (discount != null) {
            double discountPercentage = discount.getDiscountPercentage();
            rentalRate = rentalRate - (rentalRate * discountPercentage / 100);
        }

        rentalRateLabel.setText("Rental rate: $" + String.format("%.2f", rentalRate));
        if (discount != null) {
            rentalRateLabel.setStyle("-fx-text-fill: red;");
            rentalRateLabel.setText(rentalRateLabel.getText() + " (Discounted)");
        }

        // Initialize the PauseTransition with a 1-second delay, giving user time to write correct input
        pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> updateTotalCost());

        // Add a listener to the rentalDaysTF to start the pause transition on key release
        rentalDaysTF.setOnKeyReleased(event -> {
            pause.playFromStart(); // Restart the pause transition
        });        
    }

    /**
     * This method updates the total cost of the rental based on the number of days entered
     */
    @FXML
    private void updateTotalCost() {
        try {
            int rentalDays = Integer.parseInt(rentalDaysTF.getText());
            if (rentalDays <= 0 || rentalDays > selectedFilm.getRentalDuration()) {
                Platform.runLater(() -> {
                    GeneralUtils.showAlert(Alert.AlertType.ERROR, ERROR_TITLE, INVALID_INPUT_MESSAGE, "Please enter a valid number of days.");
                });
                totalCostLabel.setText("Total cost: $0.00");
                return;
            }
            totalCost = rentalDays * rentalRate;
            totalCostLabel.setText(String.format("Total cost: $%.2f", totalCost));
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                GeneralUtils.showAlert(AlertType.ERROR, ERROR_TITLE, INVALID_INPUT_MESSAGE, "You need to enter a numerical value.");
            });
            totalCostLabel.setText("Total cost: $0.00");
        }
    }

    /**
     * This method tries to rent the selected film for the specified number of days
     */
    @FXML
    private void tryToRent() {
        try {
            if (totalCost > 0) {
            rentalStartDate = LocalDateTime.now();
            rentalEndDate = rentalStartDate.plusDays(Integer.parseInt(rentalDaysTF.getText()));

            // Check for available inventory, and get inventoryId of the first available inventory item
            InventoryManager inventoryManager = new InventoryManager();
            List<Inventory> availableInventories = inventoryManager.checkForAvailableInventory(selectedFilm.getFilmId(), SessionData.getInstance().getLoggedInCustomer().getStoreId(), rentalStartDate, rentalEndDate);
                if (!availableInventories.isEmpty()) {
                    int inventoryId = availableInventories.get(0).getInventoryId();
                    int customerId = SessionData.getInstance().getLoggedInCustomer().getCustomerId();
                    int storeId = SessionData.getInstance().getLoggedInCustomer().getStoreId();

                    Rental newRental = new Rental(inventoryId, customerId, storeId, rentalStartDate, rentalEndDate);
                    SessionData.getInstance().setNewRental(newRental);

                    Payment newPayment = new Payment(customerId, storeId, totalCost);
                    SessionData.getInstance().setNewPayment(newPayment);

                    StageUtils.showPopup(
                        (Stage) selectedFilmLabel.getScene().getWindow(), 
                        "payment", 
                        "Streamify - Payment Information", 
                        Modality.WINDOW_MODAL);
                } else {
                    Platform.runLater(() -> {
                        GeneralUtils.showAlert(Alert.AlertType.ERROR, ERROR_TITLE, NO_INVENTORY_MESSAGE, "Please try again later.");
                    });
                }                
            } else {
                Platform.runLater(() -> {
                    GeneralUtils.showAlert(Alert.AlertType.ERROR, ERROR_TITLE, NO_INVENTORY_MESSAGE, "Please try again later.");
                });
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                GeneralUtils.showAlert(Alert.AlertType.ERROR, ERROR_TITLE, ERROR_MESSAGE, "Please try again.");
            });
            e.printStackTrace();
        }
    }    
}