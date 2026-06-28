package com.admission.dao;

import com.admission.model.Student;
import com.admission.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ---- CREATE ----
    public int addStudent(Student s) throws SQLException {
        String sql = """
            INSERT INTO Students
              (first_name, last_name, email, phone, date_of_birth, gender,
               address, city, state, pincode, tenth_marks, twelfth_marks, entrance_score)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  s.getFirstName());
            ps.setString(2,  s.getLastName());
            ps.setString(3,  s.getEmail());
            ps.setString(4,  s.getPhone());
            ps.setDate(5,    Date.valueOf(s.getDateOfBirth()));
            ps.setString(6,  s.getGender());
            ps.setString(7,  s.getAddress());
            ps.setString(8,  s.getCity());
            ps.setString(9,  s.getState());
            ps.setString(10, s.getPincode());
            ps.setDouble(11, s.getTenthMarks());
            ps.setDouble(12, s.getTwelfthMarks());
            ps.setDouble(13, s.getEntranceScore());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ---- READ ALL ----
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students ORDER BY merit_score DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ---- READ BY ID ----
    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM Students WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    // ---- SEARCH ----
    public List<Student> searchStudents(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT * FROM Students
            WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ?
            ORDER BY merit_score DESC
            """;
        String kw = "%" + keyword + "%";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ---- UPDATE ----
    public boolean updateStudent(Student s) throws SQLException {
        String sql = """
            UPDATE Students SET first_name=?, last_name=?, email=?, phone=?,
              date_of_birth=?, gender=?, address=?, city=?, state=?, pincode=?,
              tenth_marks=?, twelfth_marks=?, entrance_score=?
            WHERE student_id=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,  s.getFirstName());
            ps.setString(2,  s.getLastName());
            ps.setString(3,  s.getEmail());
            ps.setString(4,  s.getPhone());
            ps.setDate(5,    Date.valueOf(s.getDateOfBirth()));
            ps.setString(6,  s.getGender());
            ps.setString(7,  s.getAddress());
            ps.setString(8,  s.getCity());
            ps.setString(9,  s.getState());
            ps.setString(10, s.getPincode());
            ps.setDouble(11, s.getTenthMarks());
            ps.setDouble(12, s.getTwelfthMarks());
            ps.setDouble(13, s.getEntranceScore());
            ps.setInt(14,    s.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    // ---- DELETE ----
    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM Students WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---- MERIT LIST (top N by merit for a course's cutoff) ----
    public List<Student> getMeritList(double cutOff) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students WHERE merit_score >= ? ORDER BY merit_score DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, cutOff);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ---- MAPPER ----
    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) s.setDateOfBirth(dob.toLocalDate());
        s.setGender(rs.getString("gender"));
        s.setAddress(rs.getString("address"));
        s.setCity(rs.getString("city"));
        s.setState(rs.getString("state"));
        s.setPincode(rs.getString("pincode"));
        s.setTenthMarks(rs.getDouble("tenth_marks"));
        s.setTwelfthMarks(rs.getDouble("twelfth_marks"));
        s.setEntranceScore(rs.getDouble("entrance_score"));
        s.setMeritScore(rs.getDouble("merit_score"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }
}
