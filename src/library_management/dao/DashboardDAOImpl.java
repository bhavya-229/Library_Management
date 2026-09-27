package library_management.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import library_management.model.DashboardStats;
import library_management.util.DBConnection;

/**
 * DashboardDAOImpl - JDBC Implementation for Library Analytics
 * 
 * Demonstrates SQL Aggregation:
 * - COUNT, SUM, and COALESCE functions
 * - Conditional Aggregation with CASE WHEN
 * - GROUP BY and aggregate sorting
 */
public class DashboardDAOImpl implements DashboardDAO {

    @Override
    public DashboardStats getDashboardStatistics() throws SQLException {
        DashboardStats stats = new DashboardStats();

        try (Connection conn = DBConnection.getConnection()) {

            // Query 1: Book Inventory Aggregates (COUNT, SUM, COALESCE)
            String bookSql = "SELECT COUNT(*) AS total_titles, "
                           + "COALESCE(SUM(total_quantity), 0) AS total_copies, "
                           + "COALESCE(SUM(available_quantity), 0) AS available_copies "
                           + "FROM books";

            try (PreparedStatement stmt = conn.prepareStatement(bookSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalBookTitles(rs.getInt("total_titles"));
                    stats.setTotalPhysicalCopies(rs.getInt("total_copies"));
                    stats.setTotalAvailableCopies(rs.getInt("available_copies"));
                }
            }

            // Query 2: Total Members Count
            String memberSql = "SELECT COUNT(*) AS total_members FROM members";
            try (PreparedStatement stmt = conn.prepareStatement(memberSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalRegisteredMembers(rs.getInt("total_members"));
                }
            }

            // Query 3: Issues Circulation & Fines (Conditional Aggregation with CASE WHEN)
            String issueSql = "SELECT "
                            + "COUNT(CASE WHEN status = 'ISSUED' THEN 1 END) AS active_issues, "
                            + "COUNT(CASE WHEN status = 'ISSUED' AND due_date < CURDATE() THEN 1 END) AS overdue_issues, "
                            + "COUNT(CASE WHEN status = 'RETURNED' THEN 1 END) AS returned_issues, "
                            + "COALESCE(SUM(fine_amount), 0.0) AS total_fines "
                            + "FROM issues";

            try (PreparedStatement stmt = conn.prepareStatement(issueSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setCurrentlyIssuedBooks(rs.getInt("active_issues"));
                    stats.setOverdueBooks(rs.getInt("overdue_issues"));
                    stats.setReturnedBooks(rs.getInt("returned_issues"));
                    stats.setTotalFinesCollected(rs.getDouble("total_fines"));
                }
            }

            // Query 4: Category Distribution using SQL GROUP BY
            String categorySql = "SELECT category, COUNT(*) AS book_count "
                               + "FROM books "
                               + "GROUP BY category "
                               + "ORDER BY book_count DESC";

            Map<String, Integer> categoryMap = new LinkedHashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(categorySql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categoryMap.put(rs.getString("category"), rs.getInt("book_count"));
                }
            }
            stats.setCategoryDistribution(categoryMap);
        }

        return stats;
    }
}
