package org.example;

import database.ProgressDao;
import database.TaskDao;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.awt.event.MouseEvent;
import java.util.Optional;

public class TaskBlock extends VBox {
    private int taskId=-1;
    private double anchorX;
    private double anchorY;
    private String subjectName;
    private int duration;
    private int startRow;
    private int startColumn;
    private int rowSpan;
    private boolean isComplete;
    private String originalColor;
    private static boolean[][] addedBlocks = new boolean[25][7];
    private DashboardController controller;


    public TaskBlock(String subjName, int duration, String hexColor,int startRow,int startColumn,DashboardController controller) {

        this.subjectName=subjName;
        this.duration=duration;
        this.originalColor=hexColor;
        this.controller=controller;

        this.setStyle("-fx-background-color: " + hexColor + "; -fx-background-radius: 8;");
        this.getStylesheets().add("task-block");
        this.setPadding(new Insets(5,10,5,10));

        this.setPrefHeight(duration);
        this.setMaxWidth(Double.MAX_VALUE);

        Label subj = new Label(subjName);
        subj.setStyle("-fx-font-weight: bold; -fx-font-family: Times New Roman; -fx-text-fill: white; -fx-font-size: 12px;");

        Label durLabel=new Label(duration+" mins");
        durLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        this.getChildren().addAll(subj,durLabel);
        rowSpan = (int)Math.ceil(duration/60.0);
        GridPane.setRowSpan(this,rowSpan);

        this.startRow=startRow;
        this.startColumn=startColumn;
        updateArray(startRow,startColumn,true);
        makeBlockMove();
    }

    // DATABASE CONSTRUCTOR
    public TaskBlock(int taskId, String subjName, int duration, String hexColor, int startRow, int startColumn, boolean isComplete) {
        this.taskId = taskId;
        this.subjectName = subjName;
        this.duration = duration;
        this.originalColor = hexColor;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.isComplete = isComplete;

        // Set colors based on if it was saved as complete or not
        if (isComplete) {
            this.setStyle("-fx-background-color: #808080; -fx-background-radius: 8;");
            this.setOpacity(0.7);
        } else {
            this.setStyle("-fx-background-color: " + hexColor + "; -fx-background-radius: 8;");
        }
        this.getStylesheets().add(getClass().getResource("/dashboard.css").toExternalForm());
        this.getStylesheets().add("task-block");

        this.setPadding(new Insets(5,10,5,10));
        this.setPrefHeight(duration);
        this.setMaxWidth(Double.MAX_VALUE);

        Label subj = new Label(subjName);
        subj.setStyle("-fx-font-weight: bold; -fx-font-family: Times New Roman; -fx-text-fill: white; -fx-font-size: 12px;");

        Label durLabel=new Label(duration+" mins");
        durLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        this.getChildren().addAll(subj,durLabel);

        rowSpan = (int)Math.ceil(duration/60.0);
        GridPane.setRowSpan(this,rowSpan);

        updateArray(startRow,startColumn,true);
        makeBlockMove(); // The lambdas inside here won't crash because the user can't click them until the UI loads anyway!
    }
    public void setController(DashboardController controller) {
        this.controller = controller;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public void setOriginalColor(String hex){
        this.originalColor=hex;
    }
    public String getColour(){
        return this.originalColor;
    }




    private void makeBlockMove(){
        // when u press a box this is executed
        this.setOnMousePressed(event->{
            Integer currColumn = GridPane.getColumnIndex(this);
            Integer currRow = GridPane.getRowIndex(this);
            if(currColumn==null){startColumn=0;
            }
            else{
                startColumn=currColumn;
            }
            if(currRow==null){
                startRow=0;
            }
            else{
                startRow=currRow;
            }

            // Original Box position relative to the mouse
            anchorX=event.getSceneX()-this.getTranslateX();
            anchorY=event.getSceneY()-this.getTranslateY();


            // bring box to the top of scene
            this.toFront();
            // make box slighlty transparent
            this.setOpacity(0.7);
            updateArray(startRow,startColumn,false);

        });
        // when u drag the box this is executed
        this.setOnMouseDragged(event->{
            // calculate new box position relative to original position to create drag effect
            Region parentGrid =(Region)this.getParent();

            double rightWall=parentGrid.getWidth()-this.getWidth();
            double bottomWall=parentGrid.getHeight()-this.getHeight();

            double propX=event.getSceneX()-anchorX;
            double propY=event.getSceneY()-anchorY;
            double clampX=Math.max(0,Math.min(propX,rightWall));
            double clampY=Math.max(0,Math.min(propY,bottomWall));
            this.setTranslateX(clampX);
            this.setTranslateY(clampY);


        });
        //when u relaese the mouse
        this.setOnMouseReleased(event->{

            this.setOpacity(1);
            Region parentGrid = (Region)this.getParent();
            Point2D screenCoords = parentGrid.sceneToLocal(event.getSceneX(),event.getSceneY());
            double coordX = screenCoords.getX();
            double coordY = screenCoords.getY();
            int newCol=0;
            int newRow=0;
            this.setTranslateX(0);
            this.setTranslateY(0);
            if(coordX<80||coordY<40){
                GridPane.setRowIndex(this,startRow);
                GridPane.setColumnIndex(this,startColumn);
                updateArray(startRow,startColumn,true);
            }
            else{
                 newCol = 1 + (int)(coordX-80)/120;
                 newRow = 1 + (int)(coordY-40)/60;
                 boolean isSpaceFree=true;

                 for(int i=0;i<rowSpan;i++){
                     if(newRow+i<25 && addedBlocks[newRow+i][newCol]){
                         isSpaceFree=false;
                         break;
                     }
                 }

                if(isSpaceFree) {
                    GridPane.setColumnIndex(this,newCol);
                    GridPane.setRowIndex(this,newRow);

                    startColumn=newCol;
                    startRow=newRow;
                    new Thread(()->{
                        updateArray(startRow,startColumn,true);
                        Platform.runLater(()->{
                            if(TaskDao.updateRowColumnIndex(taskId,startRow,startColumn)){
                                System.out.println("Task added");
                            }
                            else{
                                System.out.println("Task not added");
                            }
                        });
                    }).start();

                }
                else{
                    GridPane.setRowIndex(this,startRow);
                    GridPane.setColumnIndex(this,startColumn);
                    updateArray(startRow,startColumn,true);
                }
            }
        });
        // Task Completion Logic
        this.setOnMouseClicked(event->{



            if(event.getButton()== MouseButton.SECONDARY && !isComplete){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Task Completion");
                alert.setHeaderText("Mark as Finished");
                alert.setContentText("Do you want to mark this task as complete?");

                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent() && result.get()==ButtonType.OK){

                    this.isComplete=true;
                    this.setStyle("-fx-background-color: #808080; -fx-background-radius: 8;");
                    this.setOpacity(0.7);
                    this.controller.updateProgress(this.duration);
                    new Thread(()->{
                        String taskTrait = PromptController.getTraitNameFromAI(this.subjectName);
                        Platform.runLater(()->{
                            UserSession.addXP(taskTrait,25.0);
                            ProgressDao.saveTraitProgress(UserSession.getUserID(),UserSession.getTrait(taskTrait));
                            if(taskId!=-1)
                                TaskDao.updateTaskStatus(taskId,isComplete);

                        });
                    }).start();
                }
                return;
            }
            if(event.getButton() == MouseButton.SECONDARY && isComplete){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Task Completion");
                alert.setHeaderText("Mark as Unfinished");
                alert.setContentText("Do you want to unmark this task?");

                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent()&&result.get()==ButtonType.OK){

                    this.isComplete=false;
                    this.setStyle("-fx-background-color:"+originalColor+"; -fx-background-radius: 8");
                    this.setOpacity(1);
                    this.controller.updateProgress(-this.duration);
                    new Thread(()->{

                        String taskTrait=PromptController.getTraitNameFromAI(this.subjectName);

                        Platform.runLater(()->{
                            UserSession.addXP(taskTrait,-25.0);
                            ProgressDao.saveTraitProgress(UserSession.getUserID(),UserSession.getTrait(taskTrait));
                            if(taskId!=-1){
                                TaskDao.updateTaskStatus(taskId,isComplete);
                            }
                        });
                    }).start();
                }
            }
            if(event.getClickCount()==1&&!isComplete){
                Alert doTask = new Alert(Alert.AlertType.CONFIRMATION);
                doTask.setTitle("Choose Task");
                doTask.setContentText("Do you want to study "+subjectName+" for "+duration+" minutes?");

                Optional<ButtonType> result = doTask.showAndWait();

                if(result.isPresent() && result.get()==ButtonType.OK){
                    controller.openTimerPopup(subjectName);
                }

            }


        });
    }

    public String getSubjectName(){
        return subjectName;
    }
    public int getDuration(){
        return duration;
    }
    public boolean getisComplete(){
        return isComplete;
    }
    public void setIsComplete(boolean b){
        this.isComplete=b;
    }

    public String getOriginalColor() {
        return originalColor;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getStartRow() {
        return startRow;
    }

    public void updateArray(int row, int col, boolean isFilled){
        if((row>0 && row <25)&&(col>0 && col<7)){
            for(int i=0;i<rowSpan;i++){
                if(row+i<25){
                    addedBlocks[row+i][col]=isFilled;
                }
            }
        }
    }
    public static int[] checkFreeSpace(int durationMin){
        int rowSpan = durationMin/60;
        if(rowSpan==0)
            rowSpan=1;

        for(int col=1;col<7;col++){
            boolean isSpaceFree=true;
            for(int row=1;row<=25-rowSpan;row++){

                for(int i=0;i<rowSpan;i++){
                    if(addedBlocks[row+i][col]){
                        isSpaceFree=false;
                        break;
                    }
                }

                if(isSpaceFree){
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }



}
