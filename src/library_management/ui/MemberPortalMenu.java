package library_management.ui;

import java.sql.SQLException;
import java.util.List;
import library_management.model.Book;
import library_management.model.IssueRecord;
import library_management.model.User;
import library_management.service.BookService;
import library_management.service.IssueService;
import library_management.util.InputUtil;

/**
 * MemberPortalMenu - Console UI for Logged-In Members
 * 
 * Provides self-service features for library patrons:
 * - Browse available books
 * - Search catalog
 * - View active loans
 * - View borrowing history and fines
 */
public class MemberPortalMenu {

    private final BookService bookService;
    private final IssueService issueService;
    private static final String TABLE_SEPARATOR = "=========================================================================================================";

    public MemberPortalMenu() {
        this.bookService = new BookService();
        this.issueService = new IssueService();
    }

    public void displayMenu(User loggedInUser) {
        boolean logout = false;
        System.out.println("\n==========================================");
        System.out.println("   WELCOME TO MEMBER PORTAL, " + loggedInUser.getUsername().toUpperCase() + "!");
        System.out.println("==========================================");

        while (!logout) {
            System.out.println("\n------------------------------------------");
            System.out.println("           MEMBER PORTAL MENU             ");
            System.out.println("------------------------------------------");
            System.out.println("1. View Available Books");
            System.out.println("2. Search Books");
            System.out.println("3. View My Currently Issued Books");
            System.out.println("4. View My Borrowing History");
            System.out.println("5. Logout");
            System.out.println("------------------------------------------");

            int choice = InputUtil.readIntInRange("Choose an option (1-5): ", 1, 5);
            switch (choice) {
                case 1:
                    viewAvailableBooks();
                    break;
                case 2:
                    searchBooks();
                    break;
                case 3:
                    viewMyActiveLoans(loggedInUser);
                    break;
                case 4:
                    viewMyHistory(loggedInUser);
                    break;
                case 5:
                    logout = true;
                    System.out.println("Logging out of Member Portal...");
                    break;
            }
        }
    }

    private void viewAvailableBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            printBookTable("AVAILABLE BOOKS CATALOG", books);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to load books: " + e.getMessage());
        }
    }

    private void searchBooks() {
        String keyword = InputUtil.readString("Enter search keyword (Title, Author, Category, or ISBN): ");
        try {
            List<Book> books = bookService.searchBooks(keyword);
            printBookTable("SEARCH RESULTS FOR: '" + keyword + "'", books);
        } catch (SQLException e) {
            System.err.println("[ERROR] Search failed: " + e.getMessage());
        }
    }

    private void viewMyActiveLoans(User user) {
        if (user.getMemberId() == null) {
            System.out.println("[!] Your user account is not linked to a Member Profile. Please contact the librarian.");
            return;
        }

        try {
            List<IssueRecord> allLoans = issueService.getIssuesByMember(user.getMemberId());
            // Filter in-memory for active loans (status == 'ISSUED')
            printIssueTable("MY CURRENTLY ISSUED BOOKS", allLoans, true);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve loans: " + e.getMessage());
        }
    }

    private void viewMyHistory(User user) {
        if (user.getMemberId() == null) {
            System.out.println("[!] Your user account is not linked to a Member Profile. Please contact the librarian.");
            return;
        }

        try {
            List<IssueRecord> allLoans = issueService.getIssuesByMember(user.getMemberId());
            printIssueTable("MY COMPLETE BORROWING HISTORY", allLoans, false);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve loan history: " + e.getMessage());
        }
    }

    private void printBookTable(String title, List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("[!] No books found.");
            return;
        }

        System.out.println("\n--- " + title + " ---");
        System.out.println(TABLE_SEPARATOR);
        System.out.printf("%-5s | %-28s | %-20s | %-16s | %-6s | %-9s%n",
                "ID", "Title", "Author", "Category", "Year", "Available");
        System.out.println(TABLE_SEPARATOR);

        for (Book b : books) {
            System.out.printf("%-5d | %-28s | %-20s | %-16s | %-6d | %-9d%n",
                    b.getBookId(),
                    truncate(b.getTitle(), 28),
                    truncate(b.getAuthor(), 20),
                    truncate(b.getCategory(), 16),
                    b.getPublicationYear(),
                    b.getAvailableQuantity());
        }
        System.out.println(TABLE_SEPARATOR);
        System.out.println("Total books: " + books.size());
    }

    private void printIssueTable(String title, List<IssueRecord> list, boolean onlyActive) {
        if (list == null || list.isEmpty()) {
            System.out.println("[!] No borrowing records found.");
            return;
        }

        System.out.println("\n--- " + title + " ---");
        System.out.println(TABLE_SEPARATOR);
        System.out.printf("%-8s | %-28s | %-12s | %-12s | %-12s | %-9s | %-8s%n",
                "Issue ID", "Book Title", "Issue Date", "Due Date", "Return Date", "Status", "Fine");
        System.out.println(TABLE_SEPARATOR);

        int count = 0;
        for (IssueRecord r : list) {
            if (onlyActive && !"ISSUED".equalsIgnoreCase(r.getStatus())) {
                continue;
            }
            count++;
            String retDateStr = r.getReturnDate() != null ? r.getReturnDate().toString() : "-";
            System.out.printf("%-8d | %-28s | %-12s | %-12s | %-12s | %-9s | $%-7.2f%n",
                    r.getIssueId(),
                    truncate(r.getBookTitle() != null ? r.getBookTitle() : "Book #" + r.getBookId(), 28),
                    r.getIssueDate(),
                    r.getDueDate(),
                    retDateStr,
                    r.getStatus(),
                    r.getFineAmount());
        }
        System.out.println(TABLE_SEPARATOR);
        if (count == 0) {
            System.out.println("[!] You have no active loans right now.");
        } else {
            System.out.println("Total records: " + count);
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
