package library_management.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import library_management.model.IssueRecord;

/**
 * IssueDAO - Data Access Object Interface for Book Loans / Returns
 */
public interface IssueDAO {

    /**
     * Executes an atomic database transaction:
     * 1. Inserts the issue record into `issues`
     * 2. Decrements `available_quantity` in `books`
     * 3. Commits the transaction (or rolls back on error)
     * 
     * @return the generated issue_id, or -1 if failed
     */
    int issueBook(int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) throws SQLException;

    /**
     * Executes an atomic database transaction for returning a book:
     * 1. Updates `issues` record (sets return_date, status = 'RETURNED', fine_amount)
     * 2. Increments `available_quantity` in `books`
     * 3. Commits the transaction (or rolls back on error)
     * 
     * @return true if successful
     */
    boolean returnBook(int issueId, LocalDate returnDate, double fineAmount) throws SQLException;

    /**
     * Retrieves an issue record by its ID, with book title and member name joined.
     */
    IssueRecord getIssueById(int issueId) throws SQLException;

    /**
     * Retrieves all currently active (status = 'ISSUED') loans.
     */
    List<IssueRecord> getActiveIssues() throws SQLException;

    /**
     * Retrieves borrowing history for a specific member.
     */
    List<IssueRecord> getIssuesByMemberId(int memberId) throws SQLException;

    /**
     * Checks if a member already has an active loan for a specific book.
     */
    boolean hasMemberBorrowedBook(int memberId, int bookId) throws SQLException;

    /**
     * Retrieves all borrowing records in the system (both active and returned).
     */
    List<IssueRecord> getAllIssues() throws SQLException;

    /**
     * Retrieves all returned book records.
     */
    List<IssueRecord> getReturnedIssues() throws SQLException;

    /**
     * Retrieves all overdue loans (status = 'ISSUED' and due_date < current date).
     */
    List<IssueRecord> getOverdueIssues() throws SQLException;
}
