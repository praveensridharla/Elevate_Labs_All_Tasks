package com.admission.service;

import com.admission.dao.ApplicationDAO;
import com.admission.dao.CourseDAO;
import com.admission.dao.StudentDAO;
import com.admission.model.Application;
import com.admission.model.Application.Status;
import com.admission.model.Course;
import com.admission.model.Student;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic layer — orchestrates DAO operations and enforces domain rules.
 */
public class AdmissionService {

    private final StudentDAO     studentDAO     = new StudentDAO();
    private final CourseDAO      courseDAO      = new CourseDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    // =====================================================================
    //  STUDENT OPERATIONS
    // =====================================================================

    public int registerStudent(Student s) throws SQLException {
        validateStudent(s);
        return studentDAO.addStudent(s);
    }

    public Student getStudent(int id) throws SQLException {
        return studentDAO.getStudentById(id);
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        return studentDAO.searchStudents(keyword);
    }

    public boolean updateStudent(Student s) throws SQLException {
        validateStudent(s);
        return studentDAO.updateStudent(s);
    }

    public boolean deleteStudent(int id) throws SQLException {
        return studentDAO.deleteStudent(id);
    }

    // =====================================================================
    //  COURSE OPERATIONS
    // =====================================================================

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.getAllCourses();
    }

    public Course getCourse(int id) throws SQLException {
        return courseDAO.getCourseById(id);
    }

    public int addCourse(Course c) throws SQLException {
        return courseDAO.addCourse(c);
    }

    public boolean updateCourse(Course c) throws SQLException {
        return courseDAO.updateCourse(c);
    }

    // =====================================================================
    //  APPLICATION OPERATIONS
    // =====================================================================

    /**
     * Submit a new application.
     * Validates: student & course exist, seats available, no duplicate.
     */
    public int applyForCourse(int studentId, int courseId) throws SQLException {
        Student s = studentDAO.getStudentById(studentId);
        if (s == null) throw new IllegalArgumentException("Student not found: " + studentId);

        Course c = courseDAO.getCourseById(courseId);
        if (c == null) throw new IllegalArgumentException("Course not found: " + courseId);
        if (c.getAvailableSeats() <= 0)
            throw new IllegalStateException("No seats available in " + c.getCourseName());

        Application app = new Application(studentId, courseId);
        return applicationDAO.addApplication(app);
    }

    public List<Application> getAllApplications() throws SQLException {
        return applicationDAO.getAllApplications();
    }

    public List<Application> getPendingApplications() throws SQLException {
        return applicationDAO.getApplicationsByStatus(Status.Pending);
    }

    public List<Application> getApplicationsByCourse(int courseId) throws SQLException {
        return applicationDAO.getApplicationsByCourse(courseId);
    }

    public List<Application> getApplicationsByStudent(int studentId) throws SQLException {
        return applicationDAO.getApplicationsByStudent(studentId);
    }

    /**
     * Admin approves an application after checking merit vs cut-off.
     */
    public boolean approveApplication(int appId, String adminName, String remarks) throws SQLException {
        List<Application> all = applicationDAO.getAllApplications();
        Application app = all.stream()
                .filter(a -> a.getApplicationId() == appId).findFirst().orElse(null);
        if (app == null) throw new IllegalArgumentException("Application not found: " + appId);

        if (app.getMeritScore() < app.getCutOffMerit()) {
            throw new IllegalStateException(String.format(
                    "Merit %.2f is below cut-off %.2f for %s",
                    app.getMeritScore(), app.getCutOffMerit(), app.getCourseName()));
        }

        boolean ok = applicationDAO.updateStatus(appId, Status.Approved, adminName, remarks);
        if (ok) courseDAO.updateAvailableSeats(app.getCourseId(), -1);
        return ok;
    }

    /**
     * Admin rejects an application.
     */
    public boolean rejectApplication(int appId, String adminName, String remarks) throws SQLException {
        return applicationDAO.updateStatus(appId, Status.Rejected, adminName, remarks);
    }

    /**
     * Admin waitlists an application.
     */
    public boolean waitlistApplication(int appId, String adminName, String remarks) throws SQLException {
        return applicationDAO.updateStatus(appId, Status.Waitlisted, adminName, remarks);
    }

    /**
     * Bulk auto-approve: approve all Pending whose merit >= course cut-off.
     */
    public int autoBulkApprove(String adminName) throws SQLException {
        List<Application> pending = getPendingApplications();
        int approved = 0;
        for (Application app : pending) {
            if (app.getMeritScore() >= app.getCutOffMerit()) {
                Course c = courseDAO.getCourseById(app.getCourseId());
                if (c != null && c.getAvailableSeats() > 0) {
                    applicationDAO.updateStatus(app.getApplicationId(), Status.Approved,
                            adminName, "Auto-approved: merit criteria met");
                    courseDAO.updateAvailableSeats(app.getCourseId(), -1);
                    approved++;
                }
            } else {
                applicationDAO.updateStatus(app.getApplicationId(), Status.Rejected,
                        adminName, "Auto-rejected: merit below cut-off");
            }
        }
        return approved;
    }

    public boolean verifyDocuments(int appId, boolean verified) throws SQLException {
        return applicationDAO.verifyDocuments(appId, verified);
    }

    // =====================================================================
    //  REPORTS
    // =====================================================================

    public List<Application> getAdmissionList() throws SQLException {
        return applicationDAO.getAdmissionList();
    }

    public List<Student> getMeritList(double cutOff) throws SQLException {
        return studentDAO.getMeritList(cutOff);
    }

    public int[] getDashboardStats() throws SQLException {
        int[] stats = new int[4];
        stats[0] = applicationDAO.countByStatus(Status.Pending);
        stats[1] = applicationDAO.countByStatus(Status.Approved);
        stats[2] = applicationDAO.countByStatus(Status.Rejected);
        stats[3] = applicationDAO.countByStatus(Status.Waitlisted);
        return stats;
    }

    // =====================================================================
    //  VALIDATION
    // =====================================================================

    private void validateStudent(Student s) {
        if (s.getFirstName() == null || s.getFirstName().isBlank())
            throw new IllegalArgumentException("First name is required.");
        if (s.getEmail() == null || !s.getEmail().contains("@"))
            throw new IllegalArgumentException("Valid email is required.");
        if (s.getTenthMarks() < 0 || s.getTenthMarks() > 100)
            throw new IllegalArgumentException("10th marks must be 0-100.");
        if (s.getTwelfthMarks() < 0 || s.getTwelfthMarks() > 100)
            throw new IllegalArgumentException("12th marks must be 0-100.");
        if (s.getEntranceScore() < 0 || s.getEntranceScore() > 100)
            throw new IllegalArgumentException("Entrance score must be 0-100.");
    }
}
