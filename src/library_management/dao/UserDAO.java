package library_management.dao;

import java.sql.SQLException;
import library_management.model.User;

/**
 * UserDAO - Interface for User Authentication & Role Persistence
 */
public interface UserDAO {

    /**
     * Authenticates a user against credentials in the database.
     * 
     * @return User object if valid, or null if invalid credentials
     */
    User authenticate(String username, String password) throws SQLException;

    /**
     * Finds the corresponding member_id for a given user_id from the members table.
     */
    Integer getMemberIdForUser(int userId) throws SQLException;
}
