package com.admission.model;

import java.time.LocalDateTime;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String department;
    private int durationYears;
    private int totalSeats;
    private int availableSeats;
    private double cutOffMerit;
    private double feePerYear;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;

    public Course() {}

    public Course(String code, String name, String dept, int duration,
                  int seats, double cutOff, double fee, String desc) {
        this.courseCode    = code;
        this.courseName    = name;
        this.department    = dept;
        this.durationYears = duration;
        this.totalSeats    = seats;
        this.availableSeats = seats;
        this.cutOffMerit   = cutOff;
        this.feePerYear    = fee;
        this.description   = desc;
        this.isActive      = true;
    }

    // ---- Getters & Setters ----
    public int getCourseId()                   { return courseId; }
    public void setCourseId(int v)             { this.courseId = v; }
    public String getCourseCode()              { return courseCode; }
    public void setCourseCode(String v)        { this.courseCode = v; }
    public String getCourseName()              { return courseName; }
    public void setCourseName(String v)        { this.courseName = v; }
    public String getDepartment()              { return department; }
    public void setDepartment(String v)        { this.department = v; }
    public int getDurationYears()              { return durationYears; }
    public void setDurationYears(int v)        { this.durationYears = v; }
    public int getTotalSeats()                 { return totalSeats; }
    public void setTotalSeats(int v)           { this.totalSeats = v; }
    public int getAvailableSeats()             { return availableSeats; }
    public void setAvailableSeats(int v)       { this.availableSeats = v; }
    public double getCutOffMerit()             { return cutOffMerit; }
    public void setCutOffMerit(double v)       { this.cutOffMerit = v; }
    public double getFeePerYear()              { return feePerYear; }
    public void setFeePerYear(double v)        { this.feePerYear = v; }
    public String getDescription()             { return description; }
    public void setDescription(String v)       { this.description = v; }
    public boolean isActive()                  { return isActive; }
    public void setActive(boolean v)           { this.isActive = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Seats: %d/%d | Cut-off: %.2f",
                courseCode, courseName, availableSeats, totalSeats, cutOffMerit);
    }
}
