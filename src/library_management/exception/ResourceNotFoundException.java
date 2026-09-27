package library_management.exception;

/**
 * ResourceNotFoundException - Thrown when a Book, Member, or Loan ID is not found.
 */
public class ResourceNotFoundException extends LibraryException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
