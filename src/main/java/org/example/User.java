package org.example;

public class User {
    private int userID;
    private String username;
    private String email;
    private String password;
    public User(int userID, String username, String email, String password){
        this.userID=userID;
        this.username=username;
        this.email=email;
        this.password=password;
    }
    User(){

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
