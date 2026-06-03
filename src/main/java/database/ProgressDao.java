package database;

import org.example.ProfileWindow;
import org.example.Trait;
import org.example.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProgressDao {

    public static boolean AddTraitData(int userid){
        String query = "INSERT INTO \"User_Progress\"(user_id,trait_name,xp_amount) VALUES(?,?,?)";
        try{
            Connection con = DatabaseInitialization.getConnection();
            try(PreparedStatement pst = con.prepareStatement(query)){

                String[] traits={"Logic", "Knowledge", "Creativity", "Communication",
                        "Memory", "Strategy", "Organization", "Focus"};

                for(String trait:traits){
                    pst.setInt(1,userid);
                    pst.setString(2,trait);
                    pst.setDouble(3,0.0);
                    pst.executeUpdate();
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage()+"Progress DAO");
        }
        return false;
    }
    public static HashMap<String, Trait> loadUserTraits(int userId) {
        HashMap<String, Trait> loadedTraits = new HashMap<>();
        String sql = "SELECT trait_name, xp_amount, required_xp, level FROM \"User_Progress\" WHERE user_id = ?";

        try (Connection conn = DatabaseInitialization.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("trait_name");
                double currentXP = rs.getDouble("xp_amount");
                double requiredXP = rs.getDouble("required_xp");
                int level = rs.getInt("level");

                // Use the new constructor you just made!
                loadedTraits.put(name, new Trait(name, currentXP, requiredXP, level));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loadedTraits;
    }
    public static void saveTraitProgress(int userId, Trait trait) {
        String sql = "UPDATE \"User_Progress\" SET xp_amount = ?, required_xp = ?, level = ? WHERE user_id = ? AND trait_name = ?";

        try (Connection conn = DatabaseInitialization.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, trait.getCurrentXP());
            pstmt.setDouble(2, trait.getRequiredXP());
            pstmt.setInt(3, trait.getLevel());
            pstmt.setInt(4, userId);
            pstmt.setString(5, trait.getName());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


