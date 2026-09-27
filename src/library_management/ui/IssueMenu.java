package library_management.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import library_management.exception.ActiveLoanException;
import library_management.exception.BookNotAvailableException;
import library_management.exception.LibraryException;
import library_management.exception.ResourceNotFoundException;
import library_management.model.IssueRecord;
import library_management.service.IssueService;
import library_management.util.InputUtil;

/**
 * IssueMenu - Console UI for Book Issuance, Returns, and Loan Tracking
 */
public class IssueMenu {

    private final IssueService issueService;
    private static final String TABLE_SEPARATOR = "===========================================================================================================================";

    public IssueMenu() {
        this.issueService = new IssueService();
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==========================================");
            System.out.println("       BOOK ISSUE & RETURN OPERATIONS     ");
            System.out.println("==========================================");
            System.out.println("1. Issue a Book");
            System.out.println("2. Return a Book (Calculate Fine)");
            System.out.println("3. View All Currently Issued Books");
            System.out.println("4. View Member Loan History");
            System.out.println("5. Back to Main Menu");
            System.out.println("==========================================");

            int choice = InputUtil.readIntInRange("Enter your choice (1-5): ", 1, 5);
            switch (choice) {
                case 1:
                    handleIssueBook();
                    break;
                case 2:
                    handleReturnBook();
                    break;
                case 3:
                    viewActiveIssues();
                    break;
                case 4:
                    viewMemberLoanHistory();
                    break;
                case 5:
                    exit = true;
                    System.out.println("Returning to Main Menu...");
                    break;
            }
        }
    }

    private void handleIssueBook() {
        System.out.println("\n--- Issue a Book ---");
        int memberId = InputUtil.readInt("Enter Member ID: ");
        int bookId = InputUtil.readInt("Enter Book ID: ");

        try {
            IssueRecord record = issueService.issueBook(bookId, memberId);
            System.out.println("\n[SUCCESS] Book issued successfully!");
            System.out.println("---------------------------------------------");
            System.out.println("Issue ID:    " + record.getIssueId());
            System.out.println("Book:        " + record.getBookTitle() + " (ID: " + record.getBookId() + ")");
            System.out.println("Member:      " + record.getMemberName() + " (ID: " + record.getMemberId() + ")");
            System.out.println("Issue Date:  " + record.getIssueDate());
            System.out.println("Due Date:    " + record.getDueDate() + " (" + IssueService.DEFAULT_LOAN_DAYS + " days loan period)");
            System.out.println("Status:      " + record.getStatus());
            System.out.println("---------------------------------------------");
        } catch (BookNotAvailableException e) {
            System.out.println("[STOCK ERROR] " + e.getMessage());
        } catch (ActiveLoanException e) {
            System.out.println("[ACTIVE LOAN CONFLICT] " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            System.out.println("[NOT FOUND] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[INPUT ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Transaction failed: " + e.getMessage());
        }
    }

    private void handleReturnBook() {
        System.out.println("\n--- Return a Book ---");
        int issueId = InputUtil.readInt("Enter Issue ID to return: ");

        LocalDate returnDate = LocalDate.now();
        System.out.println("Press ENTER to use today's return date (" + returnDate + "),");
        System.out.print("or enter custom date (YYYY-MM-DD) to test overdue fine calculation: ");
        String dateStr = InputUtil.readOptionalString("");

        if (!dateStr.isEmpty()) {
            try {
                returnDate = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("[!] Invalid date format. Defaulting to today's date (" + LocalDate.now() + ").");
                returnDate = LocalDate.now();
            }
        }

        try {
            IssueRecord record = issueService.returnBook(issueId, returnDate);

            long overdueDays = 0;
            if (record.getReturnDate().isAfter(record.getDueDate())) {
                overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), record.getReturnDate());
            }

            System.out.println("\n[SUCCESS] Book returned successfully!");
            System.out.println("---------------------------------------------");
            System.out.println("Issue ID:     " + record.getIssueId());
            System.out.println("Book:         " + record.getBookTitle());
            System.out.println("Member:       " + record.getMemberName());
            System.out.println("Issue Date:   " + record.getIssueDate());
            System.out.println("Due Date:     " + record.getDueDate());
            System.out.println("Return Date:  " + record.getReturnDate());
            System.out.println("Status:       " + record.getStatus());
            System.out.println("Overdue Days: " + overdueDays);
            System.out.printf("Fine Amount:  $%.2f (@ $%.2f/day overdue)%n", record.getFineAmount(), IssueService.DAILY_FINE_RATE);
            System.out.println("Inventory:    Available copy restored to catalog.");
            System.out.println("---------------------------------------------");

        } catch (ResourceNotFoundException e) {
            System.out.println("[NOT FOUND] " + e.getMessage());
        } catch (LibraryException e) {
            System.out.println("[RETURN ERROR] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[INPUT ERROR] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Transaction failed: " + e.getMessage());
        }
    }

    private void viewActiveIssues() {
        try {
            List<IssueRecord> list = issueService.getActiveIssues();
            printIssueTable("CURRENTLY ISSUED BOOKS", list);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve issued books: " + e.getMessage());
        }
    }

    private void viewMemberLoanHistory() {
        int memberId = InputUtil.readInt("Enter Member ID: ");
        try {
            List<IssueRecord> list = issueService.getIssuesByMember(memberId);
            printIssueTable("LOAN HISTORY FOR MEMBER ID: " + memberId, list);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private void printIssueTable(String title, List<IssueRecord> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("[!] No records found.");
            return;
        }

        System.out.println("\n--- " + title + " ---");
        System.out.println(TABLE_SEPARATOR);
        System.out.printf("%-8s | %-24s | %-18s | %-11s | %-11s | %-11s | %-9s | %-8s%n",
                "Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Return Date", "Status", "Fine");
        System.out.println(TABLE_SEPARATOR);

        for (IssueRecord r : list) {
            String retDateStr = r.getReturnDate() != null ? r.getReturnDate().toString() : "Not Ret";
            System.out.printf("%-8d | %-24s | %-18s | %-11s | %-11s | %-11s | %-9s | $%-7.2f%n",
                    r.getIssueId(),
                    truncate(r.getBookTitle() != null ? r.getBookTitle() : "Book #" + r.getBookId(), 24),
                    truncate(r.getMemberName() != null ? r.getMemberName() : "Member #" + r.getMemberId(), 18),
                    r.getIssueDate(),
                    r.getDueDate(),
                    retDateStr,
                    r.getStatus(),
                    r.getFineAmount());
        }
        System.out.println(TABLE_SEPARATOR);
        System.out.println("Total records: " + list.size());
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
