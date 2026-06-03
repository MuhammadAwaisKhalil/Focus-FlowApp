package org.example;

import database.DatabaseInitialization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // This is the magic line that loads your Scene Builder design!
        // Make sure the name matches your file exactly (login.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/login.fxml"));

        // Load the design into the window (600x400 size to match your canvas)
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);

        scene.getStylesheets().add(getClass().getResource("/login.css").toExternalForm());

        primaryStage.setTitle("FocusFlow - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}

