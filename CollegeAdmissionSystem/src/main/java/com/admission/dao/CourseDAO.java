package com.admission.dao;

import com.admission.model.Course;
import com.admission.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public int addCourse(Course course) throws SQLException {
        String sql = """
            INSERT INTO Courses
              (course_code, course_name, department, duration_years,
               total_seats, available_seats, cut_off_merit, fee_per_year, description)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDepartment());
            ps.setInt(4,    course.getDurationYears());
            ps.setInt(5,    course.getTotalSeats());
            ps.setInt(6,    course.getAvailableSeats());
            ps.setDouble(7, course.getCutOffMerit());
            ps.setDouble(8, course.getFeePerYear());
            ps.setString(9, course.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM Courses WHERE is_active = 1 ORDER BY course_code";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Course getCourseById(int id) throws SQLException {
        String sql = "SELECT * FROM Courses WHERE course_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean updateAvailableSeats(int courseId, int delta) throws SQLException {
        String sql = "UPDATE Courses SET available_seats = available_seats + ? WHERE course_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCourse(Course course) throws SQLException {
        String sql = """
            UPDATE Courses SET course_name=?, department=?, duration_years=?,
              total_seats=?, available_seats=?, cut_off_merit=?, fee_per_year=?, description=?
            WHERE course_id=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getDepartment());
            ps.setInt(3,    course.getDurationYears());
            ps.setInt(4,    course.getTotalSeats());
            ps.setInt(5,    course.getAvailableSeats());
            ps.setDouble(6, course.getCutOffMerit());
            ps.setDouble(7, course.getFeePerYear());
            ps.setString(8, course.getDescription());
            ps.setInt(9,    course.getCourseId());
            return ps.executeUpdate() > 0;
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        Course co = new Course();
        co.setCourseId(rs.getInt("course_id"));
        co.setCourseCode(rs.getString("course_code"));
        co.setCourseName(rs.getString("course_name"));
        co.setDepartment(rs.getString("department"));
        co.setDurationYears(rs.getInt("duration_years"));
        co.setTotalSeats(rs.getInt("total_seats"));
        co.setAvailableSeats(rs.getInt("available_seats"));
        co.setCutOffMerit(rs.getDouble("cut_off_merit"));
        co.setFeePerYear(rs.getDouble("fee_per_year"));
        co.setDescription(rs.getString("description"));
        co.setActive(rs.getInt("is_active") == 1);
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) co.setCreatedAt(ts.toLocalDateTime());
        return co;
    }
}
