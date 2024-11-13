package com.oap2024team7.team7mediastreamingapp;

import com.oap2024team7.team7mediastreamingapp.services.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class App extends Application {

    private static Scene scene;

	@Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("views/customer/contentmanagement/login"), 206, 256);
        stage.setTitle("Streamify - Login");
        
        // Set the application logo
        stage.getIcons().add(new Image(App.class.getResourceAsStream("/images/logo.png")));
        stage.setScene(scene);
        stage.show();
	}

	public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

	private static Parent loadFXML(String fxml) throws IOException {
		URL fxmlLocation = App.class.getResource("/" + fxml + ".fxml");
		if (fxmlLocation == null) {
			throw new IOException("FXML file not found: " + fxml + ".fxml");
		}
	
		FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
		return fxmlLoader.load();
	}	

    public static void main(String[] args) {
        DatabaseManager.updateDatabaseSchema();
        launch();
    }
}
