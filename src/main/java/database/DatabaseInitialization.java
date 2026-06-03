package database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInitialization {
    private static String URL;

    public static Connection getConnection()throws SQLException{
        Dotenv dotenv = Dotenv.load();
        URL = dotenv.get("DATABASE_URL");
        return DriverManager.getConnection(URL);
    }
}
