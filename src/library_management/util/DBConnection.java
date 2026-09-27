package library_management.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Database Connection Utility
 * 
 * Provides a centralized method to establish and retrieve a connection
 * to the MySQL database (library_db) running on XAMPP.
 */
public class DBConnection {

    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Default XAMPP MySQL password is empty

    // Static block to register the MySQL JDBC Driver once when the class is loaded
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Make sure mysql-connector-j.jar is added to the project libraries.");
            e.printStackTrace();
        }
    }

    // Private constructor to prevent direct instantiation (utility class pattern)
    private DBConnection() {
    }

    /**
     * Obtains a new active connection to the library_db database.
     * 
     * @return Connection object representing the active session with MySQL
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
