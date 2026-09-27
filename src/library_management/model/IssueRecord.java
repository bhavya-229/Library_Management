package library_management.model;

import java.time.LocalDate;

/**
 * IssueRecord - Model / Domain Entity for Book Loans
 * 
 * Represents a record in the `issues` table and includes joined helper
 * properties (bookTitle, memberName) for user-friendly console display.
 */
public class IssueRecord {

    private int issueId;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // "ISSUED" or "RETURNED"
    private double fineAmount;

    // Optional joined fields for displaying names instead of just IDs
    private String bookTitle;
    private String memberName;

    public IssueRecord() {
    }

    // Constructor for creating a new issue
    public IssueRecord(int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = "ISSUED";
        this.fineAmount = 0.00;
    }

    // Full constructor when reading from database
    public IssueRecord(int issueId, int bookId, int memberId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, String status, double fineAmount) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount;
    }

    // --- Getters and Setters ---

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return String.format("IssueRecord [ID=%d, BookId=%d, MemberId=%d, IssueDate=%s, DueDate=%s, Status='%s']",
                issueId, bookId, memberId, issueDate, dueDate, status);
    }
}
