package com.oap2024team7.team7mediastreamingapp.controllers.customer.contentmanagement;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the fullscreen playback view
 * This class is responsible for handling the user input and controlling the playback of the selected film
 * @author Mari Østern and Kaisa Bakstad (@Marisolos and @Kaisab93)
 */

public class PlaybackController implements Initializable {

    @FXML
    private Button closeButton;

    @FXML
    private StackPane playPauseIcon;

    @FXML
    private Text playText;

    @FXML
    private Text pauseText;

    private boolean isPlaying = false;  // Track play/pause state
    private PauseTransition hideButtonTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PlaybackController initialized");

        // Set up a timer to hide the play/pause button and cursor after 1 seconds
        hideButtonTimer = new PauseTransition(Duration.seconds(1));
        hideButtonTimer.setOnFinished(event -> {
            playPauseIcon.setVisible(false);
            playPauseIcon.getScene().setCursor(Cursor.NONE); // Hide cursor
        });

        // Add a listener to the playPauseIcon to wait until the scene is ready
        playPauseIcon.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Add the mouse moved event listener to show/hide the play/pause button
                newScene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> handleMouseMoved());
            }
        });

        // Set an on-click handler for toggling play/pause state
        playPauseIcon.setOnMouseClicked(event -> togglePlayPause());
    }

    // Show play/pause button and cursor on mouse movement, then restart hide timer
    private void handleMouseMoved() {
        playPauseIcon.setVisible(true);
        playPauseIcon.getScene().setCursor(Cursor.DEFAULT); // Show cursor
        hideButtonTimer.playFromStart();
    }

    // Toggle play/pause text visibility and state
    private void togglePlayPause() {
        isPlaying = !isPlaying;

        // Show play text if paused, pause text if playing
        playText.setVisible(!isPlaying);
        pauseText.setVisible(isPlaying);
    }

    // Method to close the fullscreen playback view
    @FXML
    private void closeFullScreen() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}