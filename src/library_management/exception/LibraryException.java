package library_management.exception;

/**
 * LibraryException - Base Domain Exception
 * 
 * Demonstrates:
 * - OOP Inheritance: Subclasses RuntimeException so callers can catch
 *   domain-specific library errors.
 */
public class LibraryException extends RuntimeException {

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
