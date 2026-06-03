package org.example;

public class Trait {
    private String name;
    private double currentXP;
    private double requiredXP;
    private int level;
    public Trait(String name){
        this.name=name;
        this.currentXP=0;
        this.requiredXP=100;
        this.level=1;
    }
    public Trait(String name, double currentXP, double requiredXP, int level) {
        this.name = name;
        this.currentXP = currentXP;
        this.requiredXP = requiredXP;
        this.level = level;
    }
    public String getName(){return name;}
    public double getCurrentXP(){return currentXP;}
    public double getRequiredXP() {return requiredXP;}
    public int getLevel() {return level;}

    public void addXP(double amount){
        currentXP+=amount;
        if(currentXP<0){
            currentXP=0;
        }
        if(currentXP>=requiredXP){
            level++;
            currentXP-=requiredXP;

            requiredXP*=1.2;
        }
    }
    public double calculateProgress(){
        return currentXP/requiredXP;
    }

}
