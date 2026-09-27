package library_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import library_management.model.User;
import library_management.util.DBConnection;

/**
 * UserDAOImpl - JDBC Implementation for Authentication
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null; // Authentication failed
    }

    @Override
    public Integer getMemberIdForUser(int userId) throws SQLException {
        String sql = "SELECT member_id FROM members WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("member_id");
                }
            }
        }
        return null;
    }
}
