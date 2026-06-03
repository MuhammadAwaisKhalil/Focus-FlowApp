package org.example;

import database.ProgressDao;
import database.TaskDao;
import database.UserDao;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardController implements Initializable {
    @FXML
    private Button logoutButton;
    @FXML
    private Button makeSchedule;
    @FXML
    private Button showSchedule;
    @FXML
    private Button makeSummary;
    @FXML
    private Label showText;
    @FXML
    private BorderPane dashboardScreen;
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Circle picCircle;
    @FXML
    private VBox sideTaskBar;
    @FXML
    private TextField taskField;
    @FXML
    private TextField durationField;
    private int totalMinutes;
    private int completedMinutes;
    private int timeLeft;
    private boolean isWorking;
    private Stage breakScreen;


    public DashboardController() {

    }

    @FXML
    private void createSchedule(ActionEvent e) throws IOException {
        //showText.setText("Makin schedule");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/input_prompt.fxml"));
        VBox promptScene = (VBox) loader.load();
        PromptController promptController = loader.getController();
        promptController.setDashboardController(this);

        dashboardScreen.setCenter(promptScene);

    }

    @FXML
    private void seeSchedule() throws IOException {
        //showText.setText("Showin schedule");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/calender.fxml"));
        try {
            ScrollPane scrollView = loader.load();

            // 2. Put the ScrollPane in the center of the dashboard
            dashboardScreen.setCenter(scrollView);

            // 3. Extract the GridPane from inside the ScrollPane
            GridPane gridView = (GridPane) scrollView.getContent();
            if ( UserSession.getUserTasks() != null && !UserSession.getUserTasks().isEmpty() ) {

                for (TaskBlock assignment : UserSession.getUserTasks()) {
                    if (!gridView.getChildren().contains(assignment)){
                        ((GridPane) gridView).add(assignment, assignment.getStartColumn(), assignment.getStartRow());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void generateSummary() throws IOException {
        StringBuilder completedTasks = new StringBuilder();
        StringBuilder incompleteTasks = new StringBuilder();
        for (TaskBlock task : UserSession.getUserTasks()) {
            try {
                if (task.getisComplete()) {
                    completedTasks.append(task.getSubjectName()).append(" (").append(task.getDuration()).append("mins) ");
                } else {
                    incompleteTasks.append(task.getSubjectName()).append(" (").append(task.getDuration()).append("mins) ");
                }
            } catch (Exception e) {
                System.out.println(e.fillInStackTrace().getMessage());
            }
        }
        String completed = !completedTasks.isEmpty() ? completedTasks.toString() : "None";
        String incomplete = !incompleteTasks.isEmpty() ? incompleteTasks.toString() : "None";

        String prompt = "Act as a motivating and wise study coach who would tell me areas of improvement" +
                " and what the next approach should be. Here is my progress till now" +
                "Completed tasks: " + completed +
                "Missed Tasks: " + incomplete +
                "Write a motivating, critical summary of my daily progress and tell me best practice" +
                " for next day based on my performance. Response should not exceed 5 lines.";
        if (!completedTasks.isEmpty() || !incompleteTasks.isEmpty()) {
            new Thread(() -> {
                String response = PromptController.getSummaryFromAI(prompt);
                Platform.runLater(() -> {
                    Alert showSummary = new Alert(Alert.AlertType.INFORMATION);
                    showSummary.setTitle("Daily Summary");
                    showSummary.setContentText(response);
                    showSummary.showAndWait();
                    System.out.println(response);
                });
            }).start();


        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Daily Summary");
            alert.setContentText("You have not decided upon any tasks yet!");
            alert.showAndWait();
        }


    }

    @FXML
    private void takeQuiz() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizwindow.fxml"));
        TextInputDialog askSubject = new TextInputDialog();
        askSubject.setTitle("Quiz Generator");
        askSubject.setContentText("What do you want to quiz yourself on?");
        Optional<String> result = askSubject.showAndWait();
        // type and click ok your result stores here
        result.ifPresent(topic -> {
            if (topic.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("No Subject Given!");
                alert.showAndWait();
                return;
            }

            new Thread(() -> {
                ArrayList<QuizQuestion> quiz = PromptController.generateQuizFromAI(topic, 5);

                Platform.runLater(() -> {
                    if (quiz != null && !quiz.isEmpty()) {

                        try {
                            Node quizView = loader.load();
                            QuizController controller = loader.getController();
                            controller.setQuizData(quiz, () -> {
                                try {
                                    seeSchedule();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            dashboardScreen.setCenter(quizView);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Something went wrong. Please try again");
                        alert.showAndWait();
                    }
                });

            }).start();
        });


    }

    @FXML
    private void checkLogout(ActionEvent e) throws IOException {
        FXMLLoader loginfxml = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene loginScene = new Scene(loginfxml.load(), 600, 400);
        loginScene.getStylesheets().add(getClass().getResource("/login.css").toExternalForm());
        Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        currentStage.setScene(loginScene);
        currentStage.setTitle("Focus Flow-Login");
        currentStage.show();

        UserSession.clearSession();
    }

    public void openTimerPopup(String subjectName) {
        Stage timerStage = new Stage();
        timerStage.setTitle("Focus Flow-Study Mode");
        Label titleLabel = new Label("Pomodoro Timer: " + subjectName);
        titleLabel.setStyle("-fx-font-size: 2px; -fx-text-fill: #333333; -fx-font-weight: bold;");
        Label timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: #e74c3c");


        Timeline pomodoroTimer = new Timeline();

        Button returnButton = new Button("Return to Dashboard");
        returnButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 15px;");

        returnButton.setOnAction(e -> {
            pomodoroTimer.stop();
            timerStage.close();
            MusicPlayer.pause();
            hideBreakScreen();
        });

        Button pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 15px;");
        pauseButton.setOnAction(actionEvent -> {
            if (pomodoroTimer.getStatus() == Animation.Status.RUNNING) {
                pomodoroTimer.pause();
                pauseButton.setText("Resume");
                pauseButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 15px;");
            } else {
                pomodoroTimer.play();
                pauseButton.setText("Pause");
                pauseButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 15px;");
            }
        });

        ToggleButton musicOption = new ToggleButton("Enable Music");
        ComboBox<String> musicList = new ComboBox<>();
        musicList.getItems().addAll("Rain", "Fireplace", "Lo-Fi Beats");
        musicList.setValue("Rain");
        musicList.setVisible(false);

        musicOption.setOnAction(event -> {
            if (musicOption.isSelected()) {
                musicOption.setText("Disable Music");
                musicOption.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                musicList.setVisible(true);
                MusicPlayer.loadAudio(musicList.getValue());
                MusicPlayer.play();
            } else {
                musicOption.setText("Enable Music");
                musicOption.setStyle("");
                musicList.setVisible(false);
                MusicPlayer.pause();
            }
        });

        musicList.setOnAction(event -> {
            if (musicOption.isSelected()) {
                MusicPlayer.pause();
                MusicPlayer.loadAudio(musicList.getValue());
                MusicPlayer.play();
            }
        });

        HBox musicBox = new HBox(10, musicOption, musicList);
        musicBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, timerLabel, pauseButton, returnButton, musicBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: white; -fx-padding: 30px");

        Scene scene = new Scene(layout, 350, 300);
        timerStage.setScene(scene);
        timerStage.setAlwaysOnTop(true);
        timerStage.show();


        timeLeft = 25 * 60; //return to 25*60
        isWorking = true;

        pomodoroTimer.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.seconds(1), actionEvent -> {
            timeLeft--;
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

            if (timeLeft <= 0) {
                pomodoroTimer.stop();
                if (isWorking) {
                    isWorking = false;
                    timeLeft = 5 * 60; // return to 5*60
                    titleLabel.setText("Break Time!");
                    timerLabel.setStyle("-fx-text-fill: #2ecc71");
                    makeScreenGrey();
                    MusicPlayer.pause();
                } else {
                    isWorking = true;
                    timeLeft = 25 * 60;
                    titleLabel.setText("Pomodoro Timer: " + subjectName);
                    timerLabel.setStyle("-fx-text-fill: #e74c3c;");
                    MusicPlayer.play();
                    hideBreakScreen();
                }
            }
            pomodoroTimer.play();
        });
        pomodoroTimer.getKeyFrames().add(frame);
        pomodoroTimer.play();

        timerStage.setOnCloseRequest(event -> {
            MusicPlayer.pause();
            pomodoroTimer.stop();

        });
    }

    public void updateProgress(int minutesChanged) {
        this.completedMinutes += minutesChanged;
        double percentage = (double) this.completedMinutes / this.totalMinutes;

        this.progressBar.setProgress(percentage);
    }

    public void renderSchedule(String aiResponse) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/calender.fxml"));
        ScrollPane scrollView = loader.load();
        dashboardScreen.setCenter(scrollView);
        GridPane gridView = (GridPane) scrollView.getContent();

        gridView.getChildren().removeIf(node -> node instanceof TaskBlock);

        for (TaskBlock task : UserSession.getUserTasks()) {
            gridView.add(task, task.getStartColumn(), task.getStartRow());
        }

        try {
            JSONArray tasks = new JSONArray(aiResponse);
            // breaks task into array whose elements are the whole{} block

            for (int i = 0; i < tasks.length(); i++) {
                // extracting single task
                JSONObject task = tasks.getJSONObject(i);

                String subject = task.getString("subject");
                int duration = task.getInt("duration");
                String color = task.getString("color");
                int row = task.getInt("row");
                int column = task.getInt("col");

                this.totalMinutes += duration;

                TaskBlock assignment = new TaskBlock(subject, duration, color, row, column, this);
                UserSession.addTask(assignment);

                TaskDao.saveTask(UserSession.getUserID(),assignment);

                gridView.add(assignment, column, row);
            }

        } catch (Exception ex) {
            System.out.println("Something wromg with JSON string parsing\n" + ex.getMessage());
        }
    }

    public void makeScreenGrey() {
        if (breakScreen == null) {
            breakScreen = new Stage();
            breakScreen.setAlwaysOnTop(true);
            breakScreen.initStyle(StageStyle.TRANSPARENT);

            Label infoLayout = new Label("Press ESC to enable screen");
            infoLayout.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold");
            StackPane layout = new StackPane(infoLayout);
            layout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");


            Scene breakScene = new Scene(layout);
            breakScene.setFill(Color.TRANSPARENT);
            breakScreen.setScene(breakScene);
            breakScene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    hideBreakScreen();
                }
            });

            var bounds = Screen.getPrimary().getVisualBounds();
            breakScreen.setX(bounds.getMinX());
            breakScreen.setY(bounds.getMinY());
            breakScreen.setWidth(bounds.getWidth());
            breakScreen.setHeight(bounds.getHeight());

        }
        breakScreen.show();
    }

    public void hideBreakScreen() {
        if (breakScreen != null) {
            breakScreen.hide();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        try {
            //Image image = new Image("https://api.dicebear.com/7.x/avataaars/png?seed=FocusFlow");
        //pic downlaods in backgriund so it doesnt lag the app if u also place true

            if (picCircle != null) {
                //picCircle.setFill(new ImagePattern(image));


                picCircle.setCursor(Cursor.HAND);
                picCircle.setOnMouseClicked(event -> {

                    ProfileWindow.openProfileWindow();
                });
            }

            }catch(Exception e) {
            System.out.println("Pic not loaded");
        }

            // Load tasks and everything else
        new Thread(()->{
            ArrayList<TaskBlock> savedTasks = TaskDao.loadUserTasks(UserSession.getUserID());
            UserSession.getUserTasks().clear();
            Platform.runLater(()->{
                for (TaskBlock task : savedTasks) {
                    // TELL THE TASK WHO THE CONTROLLER IS!
                    task.setController(this);

                    System.out.println("Runs");
                    UserSession.addTask(task);

                    GridPane.setRowIndex(task, task.getStartRow());
                    GridPane.setColumnIndex(task, task.getStartColumn());
                }
                try {
                    seeSchedule();
                } catch (IOException e) {
                    System.out.println("Problem in loading grid");
                }
            });
        }).start();


    }

    @FXML
    private void openSideTaskBar() {
        boolean sidebarStatus = sideTaskBar.isVisible();
        if (!sidebarStatus) {
            sideTaskBar.setManaged(true);
            sideTaskBar.setVisible(true);
        } else {
            sideTaskBar.setManaged(false);
            sideTaskBar.setVisible(false);
        }
    }

    @FXML
    private void createTask() {
        String taskName = taskField.getText();
        String duration = durationField.getText();
        try {
            if (taskName == null || duration == null) {
                throw new NullPointerException();
            }
            try {
                int durationMins = Integer.parseInt(duration);
                int[] position = TaskBlock.checkFreeSpace(durationMins);
                if (position == null) {
                    throw new NullPointerException("Error");
                }
                TaskBlock assignment = new TaskBlock(taskName, durationMins, "#32ede7", position[0], position[1], this);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                UserSession.addTask(assignment);
                alert.setTitle("Task Added");
                alert.setHeaderText("");
                alert.setContentText(taskName + " task successfully added!");
                taskField.clear();
                durationField.clear();
                TaskDao.saveTask(UserSession.getUserID(),assignment);
                alert.showAndWait();
                try {
                    seeSchedule();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }

        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("");
            alert.setContentText("Both fields are mandatory!");
            alert.showAndWait();
        }
    }
}


