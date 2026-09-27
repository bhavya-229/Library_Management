package library_management.service;

import java.sql.SQLException;
import library_management.dao.DashboardDAO;
import library_management.dao.DashboardDAOImpl;
import library_management.model.DashboardStats;

/**
 * DashboardService - Business Logic for System Metrics & Reports
 */
public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAOImpl();
    }

    public DashboardService(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    /**
     * Gathers all system metrics.
     */
    public DashboardStats getDashboardStatistics() throws SQLException {
        return dashboardDAO.getDashboardStatistics();
    }
}
