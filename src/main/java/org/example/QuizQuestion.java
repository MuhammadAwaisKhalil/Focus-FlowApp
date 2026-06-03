package org.example;

public class QuizQuestion {
    private String question;
    private String[] options;
    private int correctOptionIndex;
    QuizQuestion(String question,String[] options,int correctOptionIndex){
        this.question=question;
        this.options=options;
        this.correctOptionIndex=correctOptionIndex;
    }
    public String getQuestion(){
        return question;
    }
    public String[] getOptions(){
        return options;
    }
    public int getCorrectOptionIndex(){
        return correctOptionIndex;
    }

    public boolean isCorrect(int selection){
        return selection == correctOptionIndex;
    }
}
