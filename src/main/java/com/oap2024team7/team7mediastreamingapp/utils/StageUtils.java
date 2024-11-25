package com.oap2024team7.team7mediastreamingapp.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.oap2024team7.team7mediastreamingapp.controllers.customer.accountmanagement.EditProfileController;
import com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement.PrimaryController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

/**
 * Utility class for managing stages and switching scenes.
 * This class contains methods for loading FXML files and switching scenes on stages.
 * It also contains a map of short names to full FXML paths for easy access.
 * @author Agata (Agy) Olaussen (@agyCoding)
 */
public class StageUtils {

    // Map for storing short names to full FXML paths
    private static final Map<String, String> FXML_PATHS = new HashMap<>();

    // Static block to initialize paths
    static {
        FXML_PATHS.put("adminAddFilm", "/views/admin/adminaddfilm.fxml");
        FXML_PATHS.put("adminFilmManagement", "/views/admin/adminfilmmanagement.fxml");
        FXML_PATHS.put("adminPage", "/views/admin/adminpage.fxml");
        FXML_PATHS.put("adminUserManagement", "/views/admin/adminusermanagment.fxml");
        FXML_PATHS.put("editAdminAccount", "/views/admin/editaccount.fxml");

        FXML_PATHS.put("createProfile", "/views/customer/accountmanagement/createprofile.fxml");
        FXML_PATHS.put("editAccount", "/views/customer/accountmanagement/editaccount.fxml");
        FXML_PATHS.put("editProfile", "/views/customer/accountmanagement/editprofile.fxml");
        FXML_PATHS.put("manageProfiles", "/views/customer/accountmanagement/manageprofiles.fxml");
        FXML_PATHS.put("registerCustomer", "/views/customer/accountmanagement/registercustomer.fxml");

        FXML_PATHS.put("login", "/views/customer/contentmanagement/login.fxml");
        FXML_PATHS.put("primary", "/views/customer/contentmanagement/primary.fxml");
        FXML_PATHS.put("filmDetails", "/views/customer/contentmanagement/filmdetails.fxml");
        FXML_PATHS.put("myList", "/views/customer/contentmanagement/mylist.fxml");
        FXML_PATHS.put("myRentals", "/views/customer/contentmanagement/myRentals.fxml");
        FXML_PATHS.put("rentFilm", "/views/customer/contentmanagement/rentfilm.fxml");
        FXML_PATHS.put("review", "/views/customer/contentmanagement/review.fxml");
        FXML_PATHS.put("payment", "/views/customer/contentmanagement/creditcardpayment.fxml");
    }

    /**
     * Loads an FXML file and switches the scene on the current stage.
     *
     * @param currentStage The current stage to switch the scene on.
     * @param fxmlName The name of the FXML file as defined in FXML_PATHS.
     * @param title The title of the new scene.
     */
    public static void switchScene(Stage currentStage, String fxmlName, String title) {
        String fxmlPath = FXML_PATHS.get(fxmlName);
        if (fxmlPath == null) {
            System.err.println("FXML path not found for name: " + fxmlName);
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Invalid FXML path", "No path found for " + fxmlName);
            return;
        }

        try {
            Parent root = FXMLLoader.load(StageUtils.class.getResource(fxmlPath));
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the page", "An error occurred while trying to load the page.");
        }
    }
    
    public static Stage showPopup(Stage ownerStage, String fxmlName, String title, Modality modality) {
        return showPopup(ownerStage, fxmlName, title, modality, null); // Call overloaded method without controller
    }

    /**
     * Shows a pop-up window with the specified FXML file, title, and modality.
     * @param ownerStage
     * @param fxmlName
     * @param title
     * @param modality
     * @param primaryController
     * @return the Stage object of the pop-up window
     */
    public static Stage showPopup(Stage ownerStage, String fxmlName, String title, Modality modality, Object primaryController) {
        String fxmlPath = FXML_PATHS.get(fxmlName);
        if (fxmlPath == null) {
            System.err.println("FXML path not found for name: " + fxmlName);
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Invalid FXML path", "No path found for " + fxmlName);
            return null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(StageUtils.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Get the controller of the loaded FXML
            Object controller = loader.getController();
            
            // Check if primaryController is provided and set it if needed
            if (primaryController != null && controller instanceof EditProfileController) {
                ((EditProfileController) controller).setPrimaryController((PrimaryController) primaryController);
            }

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.initOwner(ownerStage);
            popupStage.initModality(modality);
            popupStage.show();

            return popupStage;

        } catch (IOException e) {
            e.printStackTrace();
            GeneralUtils.showAlert(AlertType.ERROR, "Error", "Unable to load the pop-up window", "An error occurred while trying to load the pop-up.");
            return null;
        }
    }


}
