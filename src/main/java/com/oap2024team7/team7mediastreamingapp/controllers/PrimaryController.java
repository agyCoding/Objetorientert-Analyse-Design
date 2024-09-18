package com.oap2024team7.team7mediastreamingapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PrimaryController {
        
        @FXML
        private Label welcomeLabel;
    
        public void updateUserLabel(String username) {
            welcomeLabel.setText("Welcome, " + username);
        }
}
