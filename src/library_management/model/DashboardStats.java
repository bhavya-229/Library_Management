package library_management.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DashboardStats - Data Transfer Object (DTO) for Library Statistics
 * 
 * Aggregates summary metrics calculated using SQL COUNT, SUM, and GROUP BY.
 */
public class DashboardStats {

    private int totalBookTitles;
    private int totalPhysicalCopies;
    private int totalAvailableCopies;
    private int totalRegisteredMembers;
    private int currentlyIssuedBooks;
    private int overdueBooks;
    private int returnedBooks;
    private double totalFinesCollected;
    private Map<String, Integer> categoryDistribution = new LinkedHashMap<>();

    public DashboardStats() {
    }

    public int getTotalBookTitles() {
        return totalBookTitles;
    }

    public void setTotalBookTitles(int totalBookTitles) {
        this.totalBookTitles = totalBookTitles;
    }

    public int getTotalPhysicalCopies() {
        return totalPhysicalCopies;
    }

    public void setTotalPhysicalCopies(int totalPhysicalCopies) {
        this.totalPhysicalCopies = totalPhysicalCopies;
    }

    public int getTotalAvailableCopies() {
        return totalAvailableCopies;
    }

    public void setTotalAvailableCopies(int totalAvailableCopies) {
        this.totalAvailableCopies = totalAvailableCopies;
    }

    public int getTotalRegisteredMembers() {
        return totalRegisteredMembers;
    }

    public void setTotalRegisteredMembers(int totalRegisteredMembers) {
        this.totalRegisteredMembers = totalRegisteredMembers;
    }

    public int getCurrentlyIssuedBooks() {
        return currentlyIssuedBooks;
    }

    public void setCurrentlyIssuedBooks(int currentlyIssuedBooks) {
        this.currentlyIssuedBooks = currentlyIssuedBooks;
    }

    public int getOverdueBooks() {
        return overdueBooks;
    }

    public void setOverdueBooks(int overdueBooks) {
        this.overdueBooks = overdueBooks;
    }

    public int getReturnedBooks() {
        return returnedBooks;
    }

    public void setReturnedBooks(int returnedBooks) {
        this.returnedBooks = returnedBooks;
    }

    public double getTotalFinesCollected() {
        return totalFinesCollected;
    }

    public void setTotalFinesCollected(double totalFinesCollected) {
        this.totalFinesCollected = totalFinesCollected;
    }

    public Map<String, Integer> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
}
