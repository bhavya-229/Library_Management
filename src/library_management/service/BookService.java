package library_management.service;

import java.sql.SQLException;
import java.time.Year;
import java.util.List;
import library_management.dao.BookDAO;
import library_management.dao.BookDAOImpl;
import library_management.exception.ActiveLoanException;
import library_management.exception.LibraryException;
import library_management.exception.ResourceNotFoundException;
import library_management.model.Book;

/**
 * BookService - Business Logic Layer for Book Operations
 * 
 * Demonstrates:
 * - Separation of Concerns: Sits between the UI and DAO layer.
 * - Business Rule Validation: Validates data (empty checks, ranges, duplicate ISBNs)
 *   before allowing queries to reach the database.
 */
public class BookService {

    private final BookDAO bookDAO;

    // Default constructor instantiates the standard DAO implementation
    public BookService() {
        this.bookDAO = new BookDAOImpl();
    }

    // Overloaded constructor allowing dependency injection (great for unit testing)
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Validates and registers a new book into the library catalog.
     */
    public boolean addBook(Book book) throws SQLException, IllegalArgumentException {
        validateBookFields(book);

        // Rule: ISBN should not be duplicated
        Book existing = bookDAO.getBookByIsbn(book.getIsbn());
        if (existing != null) {
            throw new IllegalArgumentException("A book with ISBN '" + book.getIsbn() + "' already exists (Title: " + existing.getTitle() + ").");
        }

        return bookDAO.addBook(book);
    }

    /**
     * Retrieves all books in the catalog.
     */
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    /**
     * Finds a book by ID.
     */
    public Book getBookById(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number.");
        }
        return bookDAO.getBookById(bookId);
    }

    /**
     * Searches books by title, author, category, or ISBN.
     */
    public List<Book> searchBooks(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.searchBooks(keyword.trim());
    }

    /**
     * Validates and updates an existing book's details.
     */
    public boolean updateBook(Book book) throws SQLException, IllegalArgumentException {
        if (book.getBookId() <= 0) {
            throw new IllegalArgumentException("Invalid Book ID.");
        }

        validateBookFields(book);

        // Ensure the book exists
        Book existing = bookDAO.getBookById(book.getBookId());
        if (existing == null) {
            throw new ResourceNotFoundException("Book with ID " + book.getBookId() + " not found.");
        }

        // Check if ISBN was changed to an ISBN owned by ANOTHER book
        Book sameIsbnBook = bookDAO.getBookByIsbn(book.getIsbn());
        if (sameIsbnBook != null && sameIsbnBook.getBookId() != book.getBookId()) {
            throw new IllegalArgumentException("Another book already uses ISBN: " + book.getIsbn());
        }

        return bookDAO.updateBook(book);
    }

    /**
     * Deletes a book, strictly enforcing that it is not currently checked out.
     */
    public boolean deleteBook(int bookId) throws SQLException, LibraryException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive number.");
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            throw new ResourceNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        // Rule: Delete a book ONLY when it is not currently issued
        if (bookDAO.isBookIssued(bookId)) {
            throw new ActiveLoanException("Cannot delete book '" + book.getTitle() + "' because it is currently issued to a member.");
        }

        return bookDAO.deleteBook(bookId);
    }

    /**
     * Validates domain constraints on book fields.
     */
    private void validateBookFields(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty.");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Book category cannot be empty.");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty.");
        }
        int currentYear = Year.now().getValue();
        if (book.getPublicationYear() < 1000 || book.getPublicationYear() > currentYear + 1) {
            throw new IllegalArgumentException("Publication year must be between 1000 and " + (currentYear + 1) + ".");
        }
        if (book.getTotalQuantity() <= 0) {
            throw new IllegalArgumentException("Total quantity must be greater than 0.");
        }
        if (book.getAvailableQuantity() < 0 || book.getAvailableQuantity() > book.getTotalQuantity()) {
            throw new IllegalArgumentException("Available quantity must be between 0 and total quantity (" + book.getTotalQuantity() + ").");
        }
    }
}
