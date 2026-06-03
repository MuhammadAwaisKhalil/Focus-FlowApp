package database;

import org.example.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public static boolean AddUserToDatabase(String user,String email,String password){
        String query = "INSERT INTO \"User\"(username,email,password) VALUES(?,?,?)";
        try{
            Connection con = DatabaseInitialization.getConnection();
            try(PreparedStatement pst = con.prepareStatement(query)){
                pst.setString(1,user);
                pst.setString(2,email);
                pst.setString(3,password);

                pst.executeUpdate();

                System.out.println("It worked and added user correclty");
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }
    public static boolean checkUserInDatabase(String email){
        String query = "SELECT email FROM \"User\" WHERE email=?";
        try{
            Connection con = DatabaseInitialization.getConnection();
            try(PreparedStatement pst = con.prepareStatement(query)){
                pst.setString(1,email);
                try(ResultSet rs = pst.executeQuery()){
                    if(rs.next()){
                        String em = rs.getString("email");
                        if(em.equals(email)){
                            System.out.println("User found");
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                    else{
                        System.out.println("Worong in db");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static User getUserInfo(String email){
        String query = "SELECT * FROM \"User\" WHERE email=?";
        try{
            Connection con = DatabaseInitialization.getConnection();
            try(PreparedStatement pst = con.prepareStatement(query)){
                pst.setString(1,email);
                try(ResultSet rs = pst.executeQuery()){
                    if(rs.next()){
                        int id=rs.getInt("user_id");
                        String username=rs.getString("username");
                        String em = rs.getString("email");
                        String password = rs.getString("password");
                        return new User(id,username,em,password);
                    }

                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage()+"In getInfo");
        }
        return null;
    }


}
