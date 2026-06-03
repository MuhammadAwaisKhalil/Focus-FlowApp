package org.example;

import database.ProgressDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;

public class QuizController {
    @FXML
    private Label questionLabel;
    @FXML
    private RadioButton option1;
    @FXML
    private RadioButton option2;
    @FXML
    private RadioButton option3;
    @FXML
    private RadioButton option4;
    @FXML
    private ToggleGroup optionsGroup;

    private ArrayList<QuizQuestion> quiz;
    private int currentQuestion;
    private int score;
    private Runnable onFinishQuiz;

    public void setQuizData(ArrayList<QuizQuestion> q,Runnable onFinishQuiz){
        quiz=q;
        this.onFinishQuiz=onFinishQuiz;
        loadQuestionToScreen();
    }
    private void loadQuestionToScreen(){
        QuizQuestion currentq = quiz.get(currentQuestion);
        questionLabel.setText(currentq.getQuestion());
        String[] options = currentq.getOptions();
        option1.setText(options[0]);
        option2.setText(options[1]);
        option3.setText(options[2]);
        option4.setText(options[3]);

        if(optionsGroup.getSelectedToggle()!=null){
            optionsGroup.getSelectedToggle().setSelected(false);
        }

    }
    @FXML
    private void checkAnswer(){
        if(optionsGroup.getSelectedToggle()==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Options");
            alert.setContentText("Please select an option to proceed");
            alert.showAndWait();
            return;
        }
        int selectedIndex=-1;
        if(option1.isSelected())
            selectedIndex=0;
        else if(option2.isSelected())
            selectedIndex=1;
        else if(option3.isSelected())
            selectedIndex=2;
        else if(option4.isSelected())
            selectedIndex=3;

        QuizQuestion question = quiz.get(currentQuestion);
        if(question.getCorrectOptionIndex()==selectedIndex){
            score+=25;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Correct");
            alert.setHeaderText("Great Job");
            alert.setContentText("You have been rewarded 25 XP!");
            alert.showAndWait();

        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong");
            alert.setHeaderText("OOPS");
            alert.setContentText("You chose the incorrect answer. The correct option is "+question.getCorrectOptionIndex()+1);
        }
        currentQuestion++;
        if(currentQuestion<5){
            loadQuestionToScreen();
        }
        else{
            endQuiz();
        }


    }
    private void endQuiz(){
        UserSession.addXP("Knowledge",score);
        ProgressDao.saveTraitProgress(UserSession.getUserID(),UserSession.getTrait("Knowledge"));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result");
        alert.setHeaderText("Congratulations");
        alert.setContentText("You have completed the quiz and added "+score+" XP to your Knowledge attribute! Keep Grinding");
        alert.showAndWait();
        if(onFinishQuiz!=null){
            onFinishQuiz.run();
        }



    }


}
