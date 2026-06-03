package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileWindow {
    private static final String[] traits={"Logic", "Knowledge", "Creativity", "Communication",
            "Memory", "Strategy", "Organization", "Focus"};
    public static void openProfileWindow(){

        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("My Profile");

        Label name = new Label(UserSession.getCurrentUser());

        Label title = new Label("Skill Levels");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox traitArea = new VBox(15);
        traitArea.setAlignment(Pos.CENTER);
        traitArea.setPadding(new Insets(20,0,20,0));

        for(String trait:traits){
            Trait currentTrait = UserSession.getTrait(trait);

            if(currentTrait!=null){
                HBox traitBar = createProgressBars(currentTrait);
                traitArea.getChildren().add(traitBar);
            }
        }

        // main layout
        VBox layout = new VBox(name,title,traitArea);


        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: white; -fx-padding: 30px;");

        Scene scene = new Scene(layout,470,600);
        stage.setScene(scene);
        stage.show();

    }
    private static HBox createProgressBars(Trait trait){
        Label traitName = new Label(trait.getName());
        traitName.setPrefWidth(110);
        traitName.setStyle("-fx-text-fill: #34495e; -fx-font-size: 14px; -fx-font-weight: bold;");

        ProgressBar traitProgress = new ProgressBar(trait.calculateProgress());
        traitProgress.setPrefWidth(200);
        traitProgress.setPrefHeight(20);
        traitProgress.setStyle("-fx-accent: #27ae60; -fx-control-inner-background: #ecf0f1;");

        Label levelIndicator = new Label("Lvl."+trait.getLevel());
        levelIndicator.setPrefWidth(50);
        levelIndicator.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");
        levelIndicator.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(traitName,traitProgress,levelIndicator);
        row.setSpacing(5);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;

    }
}
