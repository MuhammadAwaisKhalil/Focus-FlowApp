package org.example;

import database.ProgressDao;
import database.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;

    @FXML
    public void registerUser(ActionEvent e)throws IOException{
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            if (!username.trim().isEmpty() && !password.trim().isEmpty() && !email.trim().isEmpty()) {

                if(!UserDao.checkUserInDatabase(email)) {
                    UserDao.AddUserToDatabase(username, email, password);
                    UserSession.setCurrentUser(username);
                    UserSession.setCurrentUserEmail(email);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("FOCUSFLOW-Dashboard");
                    stage.show();
                    User user = UserDao.getUserInfo(email);

                    assert user != null;
                    UserSession.setUserID(user.getUserID());
                    UserSession.setCurrentUser(user.getUsername());
                    ProgressDao.AddTraitData(user.getUserID());
                    UserSession.initialize();

                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invlaid Credential");
                    alert.setHeaderText("");
                    alert.setContentText("Email/Username already taken!");
                    passwordField.clear();
                    usernameField.clear();
                    emailField.clear();
                    password=null;
                    username=null;
                    email=null;
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Fields");
                alert.setHeaderText("");
                alert.setContentText("All fields are mandatory to fullfill");
                alert.showAndWait();
                usernameField.clear();
                passwordField.clear();
                emailField.clear();
                password = null;
                email = null;
                username = null;
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }
    @FXML
    public void switchToLogin(ActionEvent e)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("FOCUS FLOW-LOGIN");
        root.getStylesheets().add("/login.css");
        stage.show();
    }

}
