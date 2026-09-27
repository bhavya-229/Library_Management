package library_management.dao;

import java.sql.SQLException;
import java.util.List;
import library_management.model.Member;

/**
 * MemberDAO - Data Access Object Interface for Members
 * 
 * Defines the contract for all Member persistence operations.
 */
public interface MemberDAO {

    /**
     * Inserts a new member record into the database.
     */
    boolean registerMember(Member member) throws SQLException;

    /**
     * Retrieves all members ordered by member ID.
     */
    List<Member> getAllMembers() throws SQLException;

    /**
     * Finds a member by their primary key ID.
     */
    Member getMemberById(int memberId) throws SQLException;

    /**
     * Finds a member by their unique email.
     */
    Member getMemberByEmail(String email) throws SQLException;

    /**
     * Searches members by matching name or email.
     */
    List<Member> searchMembers(String keyword) throws SQLException;

    /**
     * Updates an existing member's information.
     */
    boolean updateMember(Member member) throws SQLException;

    /**
     * Deletes a member by their ID.
     */
    boolean deleteMember(int memberId) throws SQLException;

    /**
     * Checks if a member currently has active (unreturned) books issued.
     * Enforces the rule: Members cannot be deleted if they have active loans.
     */
    boolean hasActiveIssues(int memberId) throws SQLException;
}
