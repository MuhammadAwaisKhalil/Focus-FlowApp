package org.example;

import database.ProgressDao;
import database.TaskDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class UserSession {
    private static String currentUser;
    private static String currentUserEmail;
    private static int userID;
    private static HashMap<String,Trait> userTraits=new HashMap<>();
    private static ArrayList<TaskBlock> userTasks=new ArrayList<>();

    UserSession(){};
    public static void initialize(){
        userTraits= ProgressDao.loadUserTraits(userID);
        if(userTraits.isEmpty()) {
            String[] traits = {"Logic", "Knowledge", "Creativity", "Communication",
                    "Memory", "Strategy", "Organization", "Focus"};
            for (String currentTrait : traits) {
                userTraits.put(currentTrait, new Trait(currentTrait));
            }
        }
    }
    public static void addXP(String name,double amount){
        userTraits.get(name).addXP(amount);
        System.out.println("XP ADDED");

        ProgressDao.saveTraitProgress(userID,userTraits.get(name));
    }
    public static Trait getTrait(String name){
        return userTraits.get(name);
    }
    public static void setCurrentUser(String user){
        currentUser=user;
    }
    public static String getCurrentUser(){
        return currentUser;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static void setCurrentUserEmail(String currentUserEmail) {
        UserSession.currentUserEmail = currentUserEmail;
    }

    public static void clearSession(){
        currentUser=null;
        currentUserEmail=null;
    }

    public static int getUserID() {
        return userID;
    }

    public static void setUserID(int userID) {
        UserSession.userID = userID;
    }

    public static void addTask(TaskBlock task){
        userTasks.add(task);
    }
    public static ArrayList<TaskBlock> getUserTasks(){
        return userTasks;
    }

    public static void setUserTraits(HashMap<String, Trait> userTraits) {
        UserSession.userTraits = userTraits;
    }
}
