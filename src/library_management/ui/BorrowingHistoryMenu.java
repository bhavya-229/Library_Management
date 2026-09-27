package library_management.ui;

import java.sql.SQLException;
import java.util.List;
import library_management.model.IssueRecord;
import library_management.service.IssueService;
import library_management.util.InputUtil;

/**
 * BorrowingHistoryMenu - Console UI for Viewing Borrowing Records & Overdue Reports
 */
public class BorrowingHistoryMenu {

    private final IssueService issueService;
    private static final String TABLE_SEPARATOR = "===========================================================================================================================";

    public BorrowingHistoryMenu() {
        this.issueService = new IssueService();
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==========================================");
            System.out.println("        BORROWING RECORDS & REPORTS       ");
            System.out.println("==========================================");
            System.out.println("1. View All Borrowing Records");
            System.out.println("2. View Currently Issued Books");
            System.out.println("3. View Returned Books");
            System.out.println("4. View Overdue Books");
            System.out.println("5. View Borrowing History by Member ID");
            System.out.println("6. Back to Main Menu");
            System.out.println("==========================================");

            int choice = InputUtil.readIntInRange("Enter your choice (1-6): ", 1, 6);
            switch (choice) {
                case 1:
                    viewAllRecords();
                    break;
                case 2:
                    viewActiveIssues();
                    break;
                case 3:
                    viewReturnedBooks();
                    break;
                case 4:
                    viewOverdueBooks();
                    break;
                case 5:
                    viewMemberHistory();
                    break;
                case 6:
                    exit = true;
                    System.out.println("Returning to Main Menu...");
                    break;
            }
        }
    }

    private void viewAllRecords() {
        try {
            List<IssueRecord> list = issueService.getAllIssues();
            printIssueTable("ALL BORROWING RECORDS", list);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve borrowing records: " + e.getMessage());
        }
    }

    private void viewActiveIssues() {
        try {
            List<IssueRecord> list = issueService.getActiveIssues();
            printIssueTable("CURRENTLY ISSUED BOOKS", list);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve active loans: " + e.getMessage());
        }
    }

    private void viewReturnedBooks() {
        try {
            List<IssueRecord> list = issueService.getReturnedIssues();
            printIssueTable("RETURNED BOOKS HISTORY", list);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to retrieve returned books: " + e.getMessage());
        }
    }

    private void viewOverdueBooks() {
        try {
            List<IssueRecord> list = issueService.getOverdueIssues();
            if (list == null || list.isEmpty()) {
                System.out.println("\n[INFO] Great news! There are currently no overdue books in the library.");
            } else {
                printIssueTable("OVERDUE BOOKS (ACTION REQUIRED)", list);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to query overdue books: " + e.getMessage());
        }
    }

    private void viewMemberHistory() {
        int memberId = InputUtil.readInt("Enter Member ID: ");
        try {
            List<IssueRecord> list = issueService.getIssuesByMember(memberId);
            printIssueTable("BORROWING HISTORY FOR MEMBER ID: " + memberId, list);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private void printIssueTable(String title, List<IssueRecord> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("\n[!] No records found for: " + title);
            return;
        }

        System.out.println("\n--- " + title + " ---");
        System.out.println(TABLE_SEPARATOR);
        System.out.printf("%-8s | %-24s | %-18s | %-11s | %-11s | %-11s | %-9s | %-8s%n",
                "Issue ID", "Book Title", "Member Name", "Issue Date", "Due Date", "Return Date", "Status", "Fine");
        System.out.println(TABLE_SEPARATOR);

        for (IssueRecord r : list) {
            String retDateStr = r.getReturnDate() != null ? r.getReturnDate().toString() : "-";
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
