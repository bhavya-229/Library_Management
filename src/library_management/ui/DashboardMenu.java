package library_management.ui;

import java.sql.SQLException;
import java.util.Map;
import library_management.model.DashboardStats;
import library_management.service.DashboardService;

/**
 * DashboardMenu - Console UI for Viewing Library Analytics & Aggregate Metrics
 */
public class DashboardMenu {

    private final DashboardService dashboardService;
    private static final String SEPARATOR = "========================================================================";

    public DashboardMenu() {
        this.dashboardService = new DashboardService();
    }

    public void displayDashboard() {
        try {
            DashboardStats stats = dashboardService.getDashboardStatistics();

            System.out.println("\n" + SEPARATOR);
            System.out.println("                     LIBRARY ANALYTICS DASHBOARD                        ");
            System.out.println(SEPARATOR);

            System.out.println(" [INVENTORY & CATALOG METRICS]");
            System.out.printf("   * Unique Book Titles:            %d%n", stats.getTotalBookTitles());
            System.out.printf("   * Total Physical Copies:         %d%n", stats.getTotalPhysicalCopies());
            System.out.printf("   * Currently Available Copies:    %d%n", stats.getTotalAvailableCopies());
            System.out.printf("   * Registered Library Members:    %d%n", stats.getTotalRegisteredMembers());

            System.out.println("\n [CIRCULATION & LOAN STATUS]");
            System.out.printf("   * Currently Issued (Active):     %d%n", stats.getCurrentlyIssuedBooks());
            System.out.printf("   * Overdue Loans:                 %d%n", stats.getOverdueBooks());
            System.out.printf("   * Completed Returns:             %d%n", stats.getReturnedBooks());
            System.out.printf("   * Total Fines Collected/Assessed:$%.2f%n", stats.getTotalFinesCollected());

            System.out.println("\n [CATEGORY DISTRIBUTION (SQL GROUP BY)]");
            if (stats.getCategoryDistribution().isEmpty()) {
                System.out.println("   (No category data available)");
            } else {
                for (Map.Entry<String, Integer> entry : stats.getCategoryDistribution().entrySet()) {
                    System.out.printf("   * %-25s  %d title(s)%n", entry.getKey() + ":", entry.getValue());
                }
            }

            System.out.println(SEPARATOR);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to generate dashboard statistics: " + e.getMessage());
        }
    }
}
