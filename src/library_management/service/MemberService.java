package library_management.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import library_management.dao.MemberDAO;
import library_management.dao.MemberDAOImpl;
import library_management.exception.ActiveLoanException;
import library_management.exception.LibraryException;
import library_management.exception.ResourceNotFoundException;
import library_management.model.Member;

/**
 * MemberService - Business Logic Layer for Member Operations
 * 
 * Enforces business rules:
 * - Email format and uniqueness
 * - Phone format
 * - Deletion restriction (cannot delete member with active book loans)
 */
public class MemberService {

    private final MemberDAO memberDAO;

    // Simple email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Phone pattern (7 to 15 digits, optionally with a leading +)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

    public MemberService() {
        this.memberDAO = new MemberDAOImpl();
    }

    public MemberService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    /**
     * Registers a new member after validating format and ensuring unique email.
     */
    public boolean registerMember(Member member) throws SQLException, IllegalArgumentException {
        validateMemberFields(member);

        if (member.getRegistrationDate() == null) {
            member.setRegistrationDate(LocalDate.now());
        }

        // Rule: Email must not be duplicated
        Member existing = memberDAO.getMemberByEmail(member.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("A member with email '" + member.getEmail() + "' is already registered (Name: " + existing.getName() + ").");
        }

        return memberDAO.registerMember(member);
    }

    /**
     * Retrieves all registered members.
     */
    public List<Member> getAllMembers() throws SQLException {
        return memberDAO.getAllMembers();
    }

    /**
     * Finds a member by ID.
     */
    public Member getMemberById(int memberId) throws SQLException {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number.");
        }
        return memberDAO.getMemberById(memberId);
    }

    /**
     * Searches members by keyword (matches name, email, or phone).
     */
    public List<Member> searchMembers(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMembers();
        }
        return memberDAO.searchMembers(keyword.trim());
    }

    /**
     * Updates an existing member's information.
     */
    public boolean updateMember(Member member) throws SQLException, IllegalArgumentException {
        if (member.getMemberId() <= 0) {
            throw new IllegalArgumentException("Invalid Member ID.");
        }

        validateMemberFields(member);

        Member existing = memberDAO.getMemberById(member.getMemberId());
        if (existing == null) {
            throw new ResourceNotFoundException("Member with ID " + member.getMemberId() + " does not exist.");
        }

        // Check if email was changed to an email already in use by another member
        Member sameEmailMember = memberDAO.getMemberByEmail(member.getEmail());
        if (sameEmailMember != null && sameEmailMember.getMemberId() != member.getMemberId()) {
            throw new IllegalArgumentException("Another member is already using the email: " + member.getEmail());
        }

        return memberDAO.updateMember(member);
    }

    /**
     * Deletes a member only if they do not have active unreturned book loans.
     */
    public boolean deleteMember(int memberId) throws SQLException, LibraryException {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number.");
        }

        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " does not exist.");
        }

        // Rule: Cannot delete member if they currently have an issued book
        if (memberDAO.hasActiveIssues(memberId)) {
            throw new ActiveLoanException("Cannot delete member '" + member.getName() + "' because they have active unreturned book loans.");
        }

        return memberDAO.deleteMember(memberId);
    }

    /**
     * Validates input fields for a member.
     */
    private void validateMemberFields(Member member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }

        if (member.getEmail() == null || !EMAIL_PATTERN.matcher(member.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format. Example: user@example.com");
        }

        if (member.getPhone() == null || !PHONE_PATTERN.matcher(member.getPhone().trim()).matches()) {
            throw new IllegalArgumentException("Invalid phone number. Must contain 7 to 15 digits.");
        }
    }
}
