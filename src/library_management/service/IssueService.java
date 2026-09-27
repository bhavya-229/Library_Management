package library_management.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import library_management.dao.BookDAO;
import library_management.dao.BookDAOImpl;
import library_management.dao.IssueDAO;
import library_management.dao.IssueDAOImpl;
import library_management.dao.MemberDAO;
import library_management.dao.MemberDAOImpl;
import library_management.exception.ActiveLoanException;
import library_management.exception.BookNotAvailableException;
import library_management.exception.LibraryException;
import library_management.exception.ResourceNotFoundException;
import library_management.model.Book;
import library_management.model.IssueRecord;
import library_management.model.Member;

/**
 * IssueService - Business Logic for Book Issuance and Loan Rules
 */
public class IssueService {

    public static final int DEFAULT_LOAN_DAYS = 14;
    public static final double DAILY_FINE_RATE = 5.0; // Configurable fine per day overdue

    private final IssueDAO issueDAO;
    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;

    public IssueService() {
        this.issueDAO = new IssueDAOImpl();
        this.bookDAO = new BookDAOImpl();
        this.memberDAO = new MemberDAOImpl();
    }

    public IssueService(IssueDAO issueDAO, BookDAO bookDAO, MemberDAO memberDAO) {
        this.issueDAO = issueDAO;
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
    }

    /**
     * Issues a book to a member with strict business validations.
     * 
     * @return the newly created IssueRecord
     */
    public IssueRecord issueBook(int bookId, int memberId) throws SQLException, IllegalArgumentException, IllegalStateException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number.");
        }
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number.");
        }

        // Rule 1: Validate Book exists
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        // Rule 2: Validate Member exists
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " does not exist.");
        }

        // Rule 3: Validate Book availability
        if (book.getAvailableQuantity() <= 0) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' is currently out of stock (Available: 0).");
        }

        // Rule 4: Prevent member from borrowing the same book twice simultaneously
        if (issueDAO.hasMemberBorrowedBook(memberId, bookId)) {
            throw new ActiveLoanException("Member '" + member.getName() + "' currently already has an active loan for '" + book.getTitle() + "'.");
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(DEFAULT_LOAN_DAYS);

        // Perform atomic transaction
        int issueId = issueDAO.issueBook(bookId, memberId, issueDate, dueDate);
        if (issueId <= 0) {
            throw new LibraryException("Failed to complete book issue transaction.");
        }

        IssueRecord record = issueDAO.getIssueById(issueId);
        return record;
    }

    /**
     * Processes a book return:
     * - Validates active loan status
     * - Calculates overdue days and fine amount
     * - Atomically updates issue record and restores book stock
     * 
     * @return the updated IssueRecord with return details
     */
    public IssueRecord returnBook(int issueId, LocalDate returnDate) throws SQLException, LibraryException {
        if (issueId <= 0) {
            throw new IllegalArgumentException("Issue ID must be a positive number.");
        }

        IssueRecord issue = issueDAO.getIssueById(issueId);
        if (issue == null) {
            throw new ResourceNotFoundException("No loan record found with Issue ID: " + issueId);
        }

        if ("RETURNED".equalsIgnoreCase(issue.getStatus())) {
            throw new LibraryException("Book has already been returned on " + issue.getReturnDate() + ".");
        }

        if (returnDate == null) {
            returnDate = LocalDate.now();
        }

        if (returnDate.isBefore(issue.getIssueDate())) {
            throw new IllegalArgumentException("Return date cannot be before the issue date (" + issue.getIssueDate() + ").");
        }

        // Calculate overdue days and fine
        long overdueDays = 0;
        if (returnDate.isAfter(issue.getDueDate())) {
            overdueDays = java.time.temporal.ChronoUnit.DAYS.between(issue.getDueDate(), returnDate);
        }

        double fineAmount = overdueDays * DAILY_FINE_RATE;

        boolean success = issueDAO.returnBook(issueId, returnDate, fineAmount);
        if (!success) {
            throw new IllegalStateException("Failed to complete book return transaction.");
        }

        return issueDAO.getIssueById(issueId);
    }

    /**
     * Retrieves all currently active (unreturned) issues.
     */
    public List<IssueRecord> getActiveIssues() throws SQLException {
        return issueDAO.getActiveIssues();
    }

    /**
     * Retrieves borrowing history for a specific member.
     */
    public List<IssueRecord> getIssuesByMember(int memberId) throws SQLException {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Member ID must be a positive number.");
        }
        return issueDAO.getIssuesByMemberId(memberId);
    }

    /**
     * Retrieves all borrowing transactions across all members.
     */
    public List<IssueRecord> getAllIssues() throws SQLException {
        return issueDAO.getAllIssues();
    }

    /**
     * Retrieves all completed/returned loan records.
     */
    public List<IssueRecord> getReturnedIssues() throws SQLException {
        return issueDAO.getReturnedIssues();
    }

    /**
     * Retrieves all currently overdue loans.
     */
    public List<IssueRecord> getOverdueIssues() throws SQLException {
        return issueDAO.getOverdueIssues();
    }
}
