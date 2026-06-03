package org.example;
import database.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;



public class loginController {
    // for scene builder and java to comm, @FXML must be with all related attributes and methods
    @FXML
    private PasswordField passwordEnter;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    private String username;
    private String email;
    private String password;

    public loginController(){

    }
    @FXML
    public void checkLogin(ActionEvent e) throws IOException {

        this.username=usernameField.getText();
        this.email=emailField.getText();
        this.password=passwordEnter.getText();


        if(username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Credentials");
            alert.setHeaderText("");
            alert.setContentText("Both Fields are Mandatory to fulfill");
            usernameField.clear();
            passwordEnter.clear();
            emailField.clear();
            username=null;
            email=null;
            password=null;
            alert.showAndWait();

        }
        if(UserDao.checkUserInDatabase(email)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("");
            alert.setContentText("Welcome to Focus-Flow");
            User user = UserDao.getUserInfo(email);
            assert user != null;
            UserSession.setUserID(user.getUserID());
            UserSession.setUserID(user.getUserID());



            FXMLLoader dashfile = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            UserSession.initialize();
            Scene dashScene = new Scene(dashfile.load(),800,600);
            Stage mainStage = (Stage)((javafx.scene.Node)e.getSource()).getScene().getWindow();
            mainStage.setScene(dashScene);
            mainStage.setTitle("FOCUS FLOW-DASHBOARD");
            UserSession.setCurrentUser(username);
            UserSession.setCurrentUserEmail(email);

            mainStage.show();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No User Found");
            alert.setHeaderText("");
            alert.setContentText("Credentials Not found. Please try again");
            usernameField.clear();
            passwordEnter.clear();
            emailField.clear();
            username=null;
            email=null;
            password=null;
            alert.showAndWait();
        }

    }
    @FXML
    private void registerNewUser(ActionEvent e)throws IOException{
        //Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegisterUser.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            java.net.URL cssURL = getClass().getResource("/login.css");
            System.out.println(cssURL);
            scene.getStylesheets().add(cssURL.toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Create Account");
            stage.show();
        }catch(Exception ex){
            System.out.println("Failed to laod register screen\n"+ex.getMessage());
        }

    }
}
