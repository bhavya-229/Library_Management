package library_management.exception;

/**
 * BookNotAvailableException - Thrown when attempting to issue a book with 0 available stock.
 */
public class BookNotAvailableException extends LibraryException {

    public BookNotAvailableException(String message) {
        super(message);
    }
}
