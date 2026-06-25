package com.admission.model;

import java.time.LocalDateTime;

public class Application {
    public enum Status { Pending, Approved, Rejected, Waitlisted }

    private int applicationId;
    private int studentId;
    private int courseId;
    private LocalDateTime applicationDate;
    private Status status;
    private String adminRemarks;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private boolean documentsVerified;

    // Joined fields (populated from queries)
    private String studentName;
    private String studentEmail;
    private double meritScore;
    private String courseName;
    private String courseCode;
    private double cutOffMerit;

    public Application() { this.status = Status.Pending; }

    public Application(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId  = courseId;
        this.status    = Status.Pending;
    }

    // ---- Getters & Setters ----
    public int getApplicationId()                { return applicationId; }
    public void setApplicationId(int v)          { this.applicationId = v; }
    public int getStudentId()                    { return studentId; }
    public void setStudentId(int v)              { this.studentId = v; }
    public int getCourseId()                     { return courseId; }
    public void setCourseId(int v)               { this.courseId = v; }
    public LocalDateTime getApplicationDate()    { return applicationDate; }
    public void setApplicationDate(LocalDateTime v) { this.applicationDate = v; }
    public Status getStatus()                    { return status; }
    public void setStatus(Status v)              { this.status = v; }
    public String getAdminRemarks()              { return adminRemarks; }
    public void setAdminRemarks(String v)        { this.adminRemarks = v; }
    public String getReviewedBy()                { return reviewedBy; }
    public void setReviewedBy(String v)          { this.reviewedBy = v; }
    public LocalDateTime getReviewedAt()         { return reviewedAt; }
    public void setReviewedAt(LocalDateTime v)   { this.reviewedAt = v; }
    public boolean isDocumentsVerified()         { return documentsVerified; }
    public void setDocumentsVerified(boolean v)  { this.documentsVerified = v; }
    public String getStudentName()               { return studentName; }
    public void setStudentName(String v)         { this.studentName = v; }
    public String getStudentEmail()              { return studentEmail; }
    public void setStudentEmail(String v)        { this.studentEmail = v; }
    public double getMeritScore()                { return meritScore; }
    public void setMeritScore(double v)          { this.meritScore = v; }
    public String getCourseName()                { return courseName; }
    public void setCourseName(String v)          { this.courseName = v; }
    public String getCourseCode()                { return courseCode; }
    public void setCourseCode(String v)          { this.courseCode = v; }
    public double getCutOffMerit()               { return cutOffMerit; }
    public void setCutOffMerit(double v)         { this.cutOffMerit = v; }

    @Override
    public String toString() {
        return String.format("[App#%d] Student:%d -> Course:%d | Status:%s",
                applicationId, studentId, courseId, status);
    }
}
