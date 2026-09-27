package library_management.dao;

import java.sql.SQLException;
import java.util.List;
import library_management.model.Book;

/**
 * BookDAO - Data Access Object Interface for Books
 * 
 * Demonstrates OOP:
 * - Abstraction & Interface Segregation: Defines WHAT operations are possible
 *   on the Book data source, hiding the underlying JDBC implementation details.
 */
public interface BookDAO {

    /**
     * Inserts a new book into the database.
     */
    boolean addBook(Book book) throws SQLException;

    /**
     * Retrieves all books from the database.
     */
    List<Book> getAllBooks() throws SQLException;

    /**
     * Finds a book by its primary key ID.
     */
    Book getBookById(int bookId) throws SQLException;

    /**
     * Finds a book by its unique ISBN.
     */
    Book getBookByIsbn(String isbn) throws SQLException;

    /**
     * Searches books by matching title, author, category, or ISBN.
     */
    List<Book> searchBooks(String keyword) throws SQLException;

    /**
     * Updates details and quantity of an existing book.
     */
    boolean updateBook(Book book) throws SQLException;

    /**
     * Deletes a book by its ID.
     */
    boolean deleteBook(int bookId) throws SQLException;

    /**
     * Checks if a book is currently actively issued (borrowed and not returned).
     * Used to prevent illegal deletion of books that are currently checked out.
     */
    boolean isBookIssued(int bookId) throws SQLException;
}
