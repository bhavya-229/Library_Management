package library_management.exception;

/**
 * ActiveLoanException - Thrown when attempting to delete a book or member with active loans,
 * or when a member attempts to borrow a book they already hold.
 */
public class ActiveLoanException extends LibraryException {

    public ActiveLoanException(String message) {
        super(message);
    }
}
