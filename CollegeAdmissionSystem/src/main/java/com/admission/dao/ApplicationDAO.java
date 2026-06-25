package com.admission.dao;

import com.admission.model.Application;
import com.admission.model.Application.Status;
import com.admission.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public int addApplication(Application app) throws SQLException {
        String sql = "INSERT INTO Applications (student_id, course_id) VALUES (?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, app.getStudentId());
            ps.setInt(2, app.getCourseId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Application> getAllApplications() throws SQLException {
        return queryWithJoin("SELECT a.*, " +
            "CONCAT(s.first_name,' ',s.last_name) AS student_name, " +
            "s.email AS student_email, s.merit_score, " +
            "c.course_name, c.course_code, c.cut_off_merit " +
            "FROM Applications a " +
            "JOIN Students s ON a.student_id = s.student_id " +
            "JOIN Courses  c ON a.course_id  = c.course_id " +
            "ORDER BY a.application_id DESC", null);
    }

    public List<Application> getApplicationsByStatus(Status status) throws SQLException {
        return queryWithJoin("SELECT a.*, " +
            "CONCAT(s.first_name,' ',s.last_name) AS student_name, " +
            "s.email AS student_email, s.merit_score, " +
            "c.course_name, c.course_code, c.cut_off_merit " +
            "FROM Applications a " +
            "JOIN Students s ON a.student_id = s.student_id " +
            "JOIN Courses  c ON a.course_id  = c.course_id " +
            "WHERE a.status = ? " +
            "ORDER BY s.merit_score DESC", status.name());
    }

    public List<Application> getApplicationsByCourse(int courseId) throws SQLException {
        String sql = "SELECT a.*, " +
            "CONCAT(s.first_name,' ',s.last_name) AS student_name, " +
            "s.email AS student_email, s.merit_score, " +
            "c.course_name, c.course_code, c.cut_off_merit " +
            "FROM Applications a " +
            "JOIN Students s ON a.student_id = s.student_id " +
            "JOIN Courses  c ON a.course_id  = c.course_id " +
            "WHERE a.course_id = ? ORDER BY s.merit_score DESC";
        List<Application> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }
        }
        return list;
    }

    public List<Application> getApplicationsByStudent(int studentId) throws SQLException {
        String sql = "SELECT a.*, " +
            "CONCAT(s.first_name,' ',s.last_name) AS student_name, " +
            "s.email AS student_email, s.merit_score, " +
            "c.course_name, c.course_code, c.cut_off_merit " +
            "FROM Applications a " +
            "JOIN Students s ON a.student_id = s.student_id " +
            "JOIN Courses  c ON a.course_id  = c.course_id " +
            "WHERE a.student_id = ?";
        List<Application> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }
        }
        return list;
    }

    public boolean updateStatus(int appId, Status status, String adminName, String remarks) throws SQLException {
        String sql = """
            UPDATE Applications SET status=?, reviewed_by=?, admin_remarks=?, reviewed_at=NOW()
            WHERE application_id=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, adminName);
            ps.setString(3, remarks);
            ps.setInt(4, appId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean verifyDocuments(int appId, boolean verified) throws SQLException {
        String sql = "UPDATE Applications SET documents_verified=? WHERE application_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, verified ? 1 : 0);
            ps.setInt(2, appId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---- Merit list: approved applications with merit >= cutoff ----
    public List<Application> getAdmissionList() throws SQLException {
        return queryWithJoin("SELECT a.*, " +
            "CONCAT(s.first_name,' ',s.last_name) AS student_name, " +
            "s.email AS student_email, s.merit_score, " +
            "c.course_name, c.course_code, c.cut_off_merit " +
            "FROM Applications a " +
            "JOIN Students s ON a.student_id = s.student_id " +
            "JOIN Courses  c ON a.course_id  = c.course_id " +
            "WHERE a.status = 'Approved' " +
            "ORDER BY c.course_code, s.merit_score DESC", null);
    }

    // ---- Stats ----
    public int countByStatus(Status status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Applications WHERE status = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ---- Helpers ----
    private List<Application> queryWithJoin(String sql, String param) throws SQLException {
        List<Application> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }
        }
        return list;
    }

    private Application mapFull(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setApplicationId(rs.getInt("application_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setCourseId(rs.getInt("course_id"));
        Timestamp appDate = rs.getTimestamp("application_date");
        if (appDate != null) a.setApplicationDate(appDate.toLocalDateTime());
        String st = rs.getString("status");
        if (st != null) a.setStatus(Status.valueOf(st));
        a.setAdminRemarks(rs.getString("admin_remarks"));
        a.setReviewedBy(rs.getString("reviewed_by"));
        Timestamp rev = rs.getTimestamp("reviewed_at");
        if (rev != null) a.setReviewedAt(rev.toLocalDateTime());
        a.setDocumentsVerified(rs.getInt("documents_verified") == 1);
        // Joined fields
        try { a.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        try { a.setStudentEmail(rs.getString("student_email")); } catch (SQLException ignored) {}
        try { a.setMeritScore(rs.getDouble("merit_score")); } catch (SQLException ignored) {}
        try { a.setCourseName(rs.getString("course_name")); } catch (SQLException ignored) {}
        try { a.setCourseCode(rs.getString("course_code")); } catch (SQLException ignored) {}
        try { a.setCutOffMerit(rs.getDouble("cut_off_merit")); } catch (SQLException ignored) {}
        return a;
    }
}
