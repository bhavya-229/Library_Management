package library_management.service;

import java.sql.SQLException;
import library_management.dao.UserDAO;
import library_management.dao.UserDAOImpl;
import library_management.model.User;

/**
 * AuthService - Business Logic for Authentication and Session Handling
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user and resolves member mapping if applicable.
     */
    public User login(String username, String password) throws SQLException, IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        // If the user has a MEMBER role, find their member_id
        if (user.isMember()) {
            Integer memberId = userDAO.getMemberIdForUser(user.getUserId());
            user.setMemberId(memberId);
        }

        return user;
    }
}
