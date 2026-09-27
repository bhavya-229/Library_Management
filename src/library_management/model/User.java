package library_management.model;

/**
 * User - Model / Domain Entity for System Authentication & Roles
 * 
 * Demonstrates:
 * - Role-Based Access Control (RBAC): Differentiates between 'ADMIN' and 'MEMBER'.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role; // "ADMIN" or "MEMBER"
    private Integer memberId; // Populated if the user is a registered member

    public User() {
    }

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isMember() {
        return "MEMBER".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return String.format("User [ID=%d, Username='%s', Role='%s']", userId, username, role);
    }
}
