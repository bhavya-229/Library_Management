package library_management.dao;

import java.sql.SQLException;
import library_management.model.DashboardStats;

/**
 * DashboardDAO - Interface for Analytical & Aggregate Database Queries
 */
public interface DashboardDAO {

    /**
     * Executes SQL aggregate functions (COUNT, SUM, GROUP BY) to gather library metrics.
     */
    DashboardStats getDashboardStatistics() throws SQLException;
}
