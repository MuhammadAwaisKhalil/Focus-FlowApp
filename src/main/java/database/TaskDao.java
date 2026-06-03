package database;
import org.example.TaskBlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
public class TaskDao {




        // 1. LOAD TASKS FROM NEON
        public static ArrayList<TaskBlock> loadUserTasks(int userId) {
            ArrayList<TaskBlock> loadedTasks = new ArrayList<>();

            String sql = "SELECT task_id, name, duration, color, start_row, start_column, is_complete FROM \"Task\" WHERE user_id = ?";

            try (Connection conn = DatabaseInitialization.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();


                while (rs.next()) {
                    int taskId = rs.getInt("task_id");
                    String name = rs.getString("name");
                    int duration = rs.getInt("duration");
                    String color = rs.getString("color");
                    int startRow = rs.getInt("start_row");
                    int startColumn = rs.getInt("start_column");
                    boolean isComplete = rs.getBoolean("is_complete");

                    // Assuming your TaskBlock constructor takes these parameters.
                    // If your constructor is different, just adjust the order here!
                    TaskBlock task = new TaskBlock(taskId,name, duration, color, startRow, startColumn, isComplete);
                    loadedTasks.add(task);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return loadedTasks;
        }

        // 2. SAVE A NEW TASK TO NEON
        public static boolean saveTask(int userId, TaskBlock task) {
            String sql = "INSERT INTO \"Task\" (user_id, name, duration, color, start_row, start_column, is_complete) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseInitialization.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, userId);
                pstmt.setString(2, task.getSubjectName());
                pstmt.setInt(3, task.getDuration());
                pstmt.setString(4, task.getColour());
                pstmt.setInt(5, task.getStartRow());
                pstmt.setInt(6, task.getStartColumn());
                pstmt.setBoolean(7, task.getisComplete());

                pstmt.executeUpdate();

                // Grab the auto-generated task_id from Neon and give it to our Java object
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    task.setTaskId(rs.getInt(1));
                }
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        // 3. MARK A TASK AS COMPLETE (Bonus method!)
        public static void updateTaskStatus(int taskId, boolean isComplete) {
            String sql = "UPDATE \"Task\" SET is_complete = ? WHERE task_id = ?";

            try (Connection conn = DatabaseInitialization.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setBoolean(1, isComplete);
                pstmt.setInt(2, taskId);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public static boolean updateRowColumnIndex(int taskID,int rowInd,int colInd){
            String sql = "UPDATE \"Task\" SET start_row=?,start_column=? WHERE task_id=?";
            try{
                Connection con = DatabaseInitialization.getConnection();
                try(PreparedStatement pst = con.prepareStatement(sql)){
                    pst.setInt(1,rowInd);
                    pst.setInt(2,colInd);
                    pst.setInt(3,taskID);

                    int r = pst.executeUpdate();
                    if(r<=0){
                        return false;
                    }
                    return true;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage()+"In update section of yask");
            }
            return false;
        }

    }

