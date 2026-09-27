package library_management;

import java.sql.SQLException;
import library_management.model.User;
import library_management.service.AuthService;
import library_management.ui.BookMenu;
import library_management.ui.BorrowingHistoryMenu;
import library_management.ui.IssueMenu;
import library_management.ui.MemberMenu;
import library_management.ui.MemberPortalMenu;
import library_management.util.InputUtil;

/**
 * Main Entry Point for Library Management System
 * 
 * Demonstrates:
 * - Authentication & Role-Based Access Control (RBAC)
 * - Differentiates between LIBRARIAN (ADMIN) and MEMBER portals
 */
public class Library_Management {

    private static final AuthService authService = new AuthService();
    private static final BookMenu bookMenu = new BookMenu();
    private static final MemberMenu memberMenu = new MemberMenu();
    private static final IssueMenu issueMenu = new IssueMenu();
    private static final BorrowingHistoryMenu historyMenu = new BorrowingHistoryMenu();
    private static final MemberPortalMenu memberPortalMenu = new MemberPortalMenu();
    private static final library_management.ui.DashboardMenu dashboardMenu = new library_management.ui.DashboardMenu();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("       LIBRARY MANAGEMENT SYSTEM (CONSOLE)       ");
        System.out.println("=================================================");

        boolean running = true;

        while (running) {
            System.out.println("\n-------------------------------------------------");
            System.out.println("                 WELCOME / LOGIN                 ");
            System.out.println("-------------------------------------------------");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.println("-------------------------------------------------");

            int choice = InputUtil.readIntInRange("Choose an option (1-2): ", 1, 2);
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    running = false;
                    System.out.println("Thank you for using the Library Management System. Goodbye!");
                    break;
            }
        }
    }

    private static void handleLogin() {
        System.out.println("\n--- User Login ---");
        String username = InputUtil.readString("Enter Username: ");
        String password = InputUtil.readString("Enter Password: ");

        try {
            User user = authService.login(username, password);
            System.out.println("\n[SUCCESS] Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");

            if (user.isAdmin()) {
                displayAdminMenu();
            } else if (user.isMember()) {
                memberPortalMenu.displayMenu(user);
            } else {
                System.out.println("[!] Unknown user role: " + user.getRole());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("[AUTH FAILED] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Failed to authenticate: " + e.getMessage());
        }
    }

    private static void displayAdminMenu() {
        boolean logout = false;
        while (!logout) {
            System.out.println("\n==========================================");
            System.out.println("          LIBRARIAN / ADMIN MENU          ");
            System.out.println("==========================================");
            System.out.println("1. Book Management");
            System.out.println("2. Member Management");
            System.out.println("3. Issue & Return Operations");
            System.out.println("4. Borrowing Records & Reports");
            System.out.println("5. Library Dashboard & Statistics");
            System.out.println("6. Logout");
            System.out.println("==========================================");

            int choice = InputUtil.readIntInRange("Choose an option (1-6): ", 1, 6);
            switch (choice) {
                case 1:
                    bookMenu.displayMenu();
                    break;
                case 2:
                    memberMenu.displayMenu();
                    break;
                case 3:
                    issueMenu.displayMenu();
                    break;
                case 4:
                    historyMenu.displayMenu();
                    break;
                case 5:
                    dashboardMenu.displayDashboard();
                    break;
                case 6:
                    logout = true;
                    System.out.println("Librarian logged out successfully.");
                    break;
            }
        }
    }
}
