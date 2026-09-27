package library_management.ui;

import java.sql.SQLException;
import java.util.List;
import library_management.model.Book;
import library_management.service.BookService;
import library_management.util.InputUtil;

/**
 * BookMenu - Console UI for Book Management
 * 
 * Demonstrates:
 * - Presentation Layer: Handles menus, user prompts, and formatted tabular outputs.
 * - Delegates all business logic to BookService.
 */
public class BookMenu {

    private final BookService bookService;

    public BookMenu() {
        this.bookService = new BookService();
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==========================================");
            System.out.println("          BOOK MANAGEMENT MENU            ");
            System.out.println("==========================================");
            System.out.println("1. View All Books");
            System.out.println("2. Search Books (Keyword / Title / Author)");
            System.out.println("3. Search Book by ID");
            System.out.println("4. Add New Book");
            System.out.println("5. Update Book Details");
            System.out.println("6. Delete Book");
            System.out.println("7. Back to Main Menu");
            System.out.println("==========================================");

            int choice = InputUtil.readIntInRange("Enter your choice (1-7): ", 1, 7);
            switch (choice) {
                case 1:
                    viewAllBooks();
                    break;
                case 2:
                    searchBooks();
                    break;
                case 3:
                    searchBookById();
                    break;
                case 4:
                    addNewBook();
                    break;
                case 5:
                    updateBook();
                    break;
                case 6:
                    deleteBook();
                    break;
                case 7:
                    exit = true;
                    System.out.println("Returning to Main Menu...");
                    break;
            }
        }
    }

    private void viewAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            printBookTable(books);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve books: " + e.getMessage());
        }
    }

    private void searchBooks() {
        String keyword = InputUtil.readString("Enter search keyword (Title, Author, Category, or ISBN): ");
        try {
            List<Book> books = bookService.searchBooks(keyword);
            printBookTable(books);
        } catch (SQLException e) {
            System.err.println("[ERROR] Search query failed: " + e.getMessage());
        }
    }

    private void searchBookById() {
        int id = InputUtil.readInt("Enter Book ID: ");
        try {
            Book book = bookService.getBookById(id);
            if (book != null) {
                System.out.println("\n--- Book Details ---");
                System.out.println("ID:                 " + book.getBookId());
                System.out.println("Title:              " + book.getTitle());
                System.out.println("Author:             " + book.getAuthor());
                System.out.println("Category:           " + book.getCategory());
                System.out.println("ISBN:               " + book.getIsbn());
                System.out.println("Publication Year:   " + book.getPublicationYear());
                System.out.println("Total Copies:       " + book.getTotalQuantity());
                System.out.println("Available Copies:   " + book.getAvailableQuantity());
            } else {
                System.out.println("[!] No book found with ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private void addNewBook() {
        System.out.println("\n--- Add New Book ---");
        String title = InputUtil.readString("Enter Title: ");
        String author = InputUtil.readString("Enter Author: ");
        String category = InputUtil.readString("Enter Category: ");
        String isbn = InputUtil.readString("Enter ISBN: ");
        int year = InputUtil.readInt("Enter Publication Year: ");
        int totalQty = InputUtil.readInt("Enter Total Quantity: ");

        Book newBook = new Book(title, author, category, isbn, year, totalQty, totalQty);
        try {
            boolean success = bookService.addBook(newBook);
            if (success) {
                System.out.println("[SUCCESS] Book added to catalog successfully!");
            } else {
                System.out.println("[!] Failed to add book.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] " + e.getMessage());
        }
    }

    private void updateBook() {
        System.out.println("\n--- Update Book ---");
        int id = InputUtil.readInt("Enter the ID of the book to update: ");
        try {
            Book existing = bookService.getBookById(id);
            if (existing == null) {
                System.out.println("[!] Book with ID " + id + " does not exist.");
                return;
            }

            System.out.println("Leave blank and press ENTER to keep current value:");
            
            System.out.print("Title [" + existing.getTitle() + "]: ");
            String title = InputUtil.readOptionalString("");
            if (!title.isEmpty()) existing.setTitle(title);

            System.out.print("Author [" + existing.getAuthor() + "]: ");
            String author = InputUtil.readOptionalString("");
            if (!author.isEmpty()) existing.setAuthor(author);

            System.out.print("Category [" + existing.getCategory() + "]: ");
            String category = InputUtil.readOptionalString("");
            if (!category.isEmpty()) existing.setCategory(category);

            System.out.print("ISBN [" + existing.getIsbn() + "]: ");
            String isbn = InputUtil.readOptionalString("");
            if (!isbn.isEmpty()) existing.setIsbn(isbn);

            System.out.print("Publication Year [" + existing.getPublicationYear() + "]: ");
            String yearStr = InputUtil.readOptionalString("");
            if (!yearStr.isEmpty()) existing.setPublicationYear(Integer.parseInt(yearStr));

            System.out.print("Total Quantity [" + existing.getTotalQuantity() + "]: ");
            String totalStr = InputUtil.readOptionalString("");
            if (!totalStr.isEmpty()) {
                int newTotal = Integer.parseInt(totalStr);
                // Adjust available quantity by difference
                int diff = newTotal - existing.getTotalQuantity();
                existing.setTotalQuantity(newTotal);
                existing.setAvailableQuantity(existing.getAvailableQuantity() + diff);
            }

            boolean success = bookService.updateBook(existing);
            if (success) {
                System.out.println("[SUCCESS] Book updated successfully!");
            } else {
                System.out.println("[!] Update failed.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[VALIDATION ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] " + e.getMessage());
        }
    }

    private void deleteBook() {
        System.out.println("\n--- Delete Book ---");
        int id = InputUtil.readInt("Enter Book ID to delete: ");
        try {
            Book book = bookService.getBookById(id);
            if (book == null) {
                System.out.println("[!] No book found with ID: " + id);
                return;
            }

            String confirm = InputUtil.readString("Are you sure you want to delete '" + book.getTitle() + "'? (yes/no): ");
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                boolean success = bookService.deleteBook(id);
                if (success) {
                    System.out.println("[SUCCESS] Book deleted successfully!");
                } else {
                    System.out.println("[!] Failed to delete book.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (IllegalStateException e) {
            System.out.println("[RESTRICTION ERROR] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[INPUT ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] " + e.getMessage());
        }
    }

    private static final String TABLE_SEPARATOR = "=========================================================================================================";

    private void printBookTable(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("[!] No books found.");
            return;
        }

        System.out.println("\n" + TABLE_SEPARATOR);
        System.out.printf("%-5s | %-28s | %-20s | %-15s | %-16s | %-6s | %-9s | %-9s%n",
                "ID", "Title", "Author", "Category", "ISBN", "Year", "Total Qty", "Avail Qty");
        System.out.println(TABLE_SEPARATOR);

        for (Book b : books) {
            System.out.printf("%-5d | %-28s | %-20s | %-15s | %-16s | %-6d | %-9d | %-9d%n",
                    b.getBookId(),
                    truncate(b.getTitle(), 28),
                    truncate(b.getAuthor(), 20),
                    truncate(b.getCategory(), 15),
                    b.getIsbn(),
                    b.getPublicationYear(),
                    b.getTotalQuantity(),
                    b.getAvailableQuantity());
        }
        System.out.println(TABLE_SEPARATOR);
        System.out.println("Total records: " + books.size());
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
