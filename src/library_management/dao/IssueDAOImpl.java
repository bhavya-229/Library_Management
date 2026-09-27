package library_management.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import library_management.model.IssueRecord;
import library_management.util.DBConnection;

/**
 * IssueDAOImpl - JDBC Implementation demonstrating ACID Transactions & SQL Joins
 */
public class IssueDAOImpl implements IssueDAO {

    @Override
    public int issueBook(int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // 1. Disable auto-commit to begin an atomic transaction
            conn.setAutoCommit(false);

            // 2. Row-level lock (FOR UPDATE) to verify stock availability safely
            String checkSql = "SELECT available_quantity FROM books WHERE book_id = ? FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, bookId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next() || rs.getInt("available_quantity") <= 0) {
                        conn.rollback();
                        return -1; // Out of stock or book missing
                    }
                }
            }

            // 3. Insert the new loan record into `issues`
            int generatedIssueId = -1;
            String insertSql = "INSERT INTO issues (book_id, member_id, issue_date, due_date, status, fine_amount) "
                             + "VALUES (?, ?, ?, ?, 'ISSUED', 0.00)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, bookId);
                insertStmt.setInt(2, memberId);
                insertStmt.setDate(3, Date.valueOf(issueDate));
                insertStmt.setDate(4, Date.valueOf(dueDate));
                insertStmt.executeUpdate();

                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedIssueId = keys.getInt(1);
                    }
                }
            }

            // 4. Decrement available stock in `books`
            String updateSql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
            }

            // 5. Commit both operations atomically
            conn.commit();
            return generatedIssueId;

        } catch (SQLException e) {
            // If any operation fails, roll back everything to keep database consistent
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Restore auto-commit state and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean returnBook(int issueId, LocalDate returnDate, double fineAmount) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Fetch book_id and verify current loan is still active
            int bookId = -1;
            String checkSql = "SELECT book_id, status FROM issues WHERE issue_id = ? FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, issueId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    if (!"ISSUED".equalsIgnoreCase(rs.getString("status"))) {
                        conn.rollback();
                        return false;
                    }
                    bookId = rs.getInt("book_id");
                }
            }

            // 2. Update issue record to RETURNED with return date and fine amount
            String updateIssueSql = "UPDATE issues SET return_date = ?, status = 'RETURNED', fine_amount = ? WHERE issue_id = ?";
            try (PreparedStatement updateIssueStmt = conn.prepareStatement(updateIssueSql)) {
                updateIssueStmt.setDate(1, Date.valueOf(returnDate));
                updateIssueStmt.setDouble(2, fineAmount);
                updateIssueStmt.setInt(3, issueId);
                updateIssueStmt.executeUpdate();
            }

            // 3. Increment book available stock
            String updateBookSql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id = ?";
            try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
            }

            // 4. Commit atomic transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public IssueRecord getIssueById(int issueId) throws SQLException {
        String sql = "SELECT i.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issues i "
                   + "INNER JOIN books b ON i.book_id = b.book_id "
                   + "INNER JOIN members m ON i.member_id = m.member_id "
                   + "WHERE i.issue_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToIssueRecord(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<IssueRecord> getActiveIssues() throws SQLException {
        List<IssueRecord> list = new ArrayList<>();
        String sql = "SELECT i.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issues i "
                   + "INNER JOIN books b ON i.book_id = b.book_id "
                   + "INNER JOIN members m ON i.member_id = m.member_id "
                   + "WHERE i.status = 'ISSUED' "
                   + "ORDER BY i.issue_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToIssueRecord(rs));
            }
        }
        return list;
    }

    @Override
    public List<IssueRecord> getIssuesByMemberId(int memberId) throws SQLException {
        List<IssueRecord> list = new ArrayList<>();
        String sql = "SELECT i.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issues i "
                   + "INNER JOIN books b ON i.book_id = b.book_id "
                   + "INNER JOIN members m ON i.member_id = m.member_id "
                   + "WHERE i.member_id = ? "
                   + "ORDER BY i.issue_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToIssueRecord(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean hasMemberBorrowedBook(int memberId, int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM issues WHERE member_id = ? AND book_id = ? AND status = 'ISSUED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setInt(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public List<IssueRecord> getAllIssues() throws SQLException {
        List<IssueRecord> list = new ArrayList<>();
        String sql = "SELECT i.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issues i "
                   + "INNER JOIN books b ON i.book_id = b.book_id "
                   + "INNER JOIN members m ON i.member_id = m.member_id "
                   + "ORDER BY i.issue_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToIssueRecord(rs));
            }
        }
        return list;
    }

    @Override
    public List<IssueRecord> getReturnedIssues() throws SQLException {
        List<IssueRecord> list = new ArrayList<>();
        String sql = "SELECT i.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issues i "
                   + "INNER JOIN books b ON i.book_id = b.book_id "
                   + "INNER JOIN members m ON i.member_id = m.member_id "
                   + "WHERE i.status = 'RETURNED' "
                   + "ORDER BY i.return_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToIssueRecord(rs));
            }
        }
        return list;
    }

    @Override
    public List<IssueRecord> getOverdueIssues() throws SQLException {
        List<IssueRecord> list = new ArrayList<>();
        String sql = "SELECT i.*, b.title AS book_title, m.name AS member_name "
                   + "FROM issues i "
                   + "INNER JOIN books b ON i.book_id = b.book_id "
                   + "INNER JOIN members m ON i.member_id = m.member_id "
                   + "WHERE i.status = 'ISSUED' AND i.due_date < CURDATE() "
                   + "ORDER BY i.due_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToIssueRecord(rs));
            }
        }
        return list;
    }

    private IssueRecord mapResultSetToIssueRecord(ResultSet rs) throws SQLException {
        Date issueDateSql = rs.getDate("issue_date");
        Date dueDateSql = rs.getDate("due_date");
        Date returnDateSql = rs.getDate("return_date");

        LocalDate issueDate = issueDateSql != null ? issueDateSql.toLocalDate() : null;
        LocalDate dueDate = dueDateSql != null ? dueDateSql.toLocalDate() : null;
        LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;

        IssueRecord record = new IssueRecord(
            rs.getInt("issue_id"),
            rs.getInt("book_id"),
            rs.getInt("member_id"),
            issueDate,
            dueDate,
            returnDate,
            rs.getString("status"),
            rs.getDouble("fine_amount")
        );

        // Map joined columns if available in the result set
        try {
            record.setBookTitle(rs.getString("book_title"));
            record.setMemberName(rs.getString("member_name"));
        } catch (SQLException ignored) {
            // Result set did not include join aliases
        }

        return record;
    }
}
