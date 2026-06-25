package com.admission.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private double tenthMarks;
    private double twelfthMarks;
    private double entranceScore;
    private double meritScore;
    private LocalDateTime createdAt;

    public Student() {}

    public Student(String firstName, String lastName, String email, String phone,
                   LocalDate dob, String gender, String address, String city,
                   String state, String pincode, double tenthMarks,
                   double twelfthMarks, double entranceScore) {
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.email         = email;
        this.phone         = phone;
        this.dateOfBirth   = dob;
        this.gender        = gender;
        this.address       = address;
        this.city          = city;
        this.state         = state;
        this.pincode       = pincode;
        this.tenthMarks    = tenthMarks;
        this.twelfthMarks  = twelfthMarks;
        this.entranceScore = entranceScore;
        this.meritScore    = calculateMerit(tenthMarks, twelfthMarks, entranceScore);
    }

    /** Must match the DB generated column formula. */
    public static double calculateMerit(double tenth, double twelfth, double entrance) {
        return (tenth * 0.20) + (twelfth * 0.50) + (entrance * 0.30);
    }

    // ---- Getters & Setters ----
    public int getStudentId()                     { return studentId; }
    public void setStudentId(int id)              { this.studentId = id; }
    public String getFirstName()                  { return firstName; }
    public void setFirstName(String v)            { this.firstName = v; }
    public String getLastName()                   { return lastName; }
    public void setLastName(String v)             { this.lastName = v; }
    public String getFullName()                   { return firstName + " " + lastName; }
    public String getEmail()                      { return email; }
    public void setEmail(String v)                { this.email = v; }
    public String getPhone()                      { return phone; }
    public void setPhone(String v)                { this.phone = v; }
    public LocalDate getDateOfBirth()             { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)       { this.dateOfBirth = v; }
    public String getGender()                     { return gender; }
    public void setGender(String v)               { this.gender = v; }
    public String getAddress()                    { return address; }
    public void setAddress(String v)              { this.address = v; }
    public String getCity()                       { return city; }
    public void setCity(String v)                 { this.city = v; }
    public String getState()                      { return state; }
    public void setState(String v)                { this.state = v; }
    public String getPincode()                    { return pincode; }
    public void setPincode(String v)              { this.pincode = v; }
    public double getTenthMarks()                 { return tenthMarks; }
    public void setTenthMarks(double v)           { this.tenthMarks = v; }
    public double getTwelfthMarks()               { return twelfthMarks; }
    public void setTwelfthMarks(double v)         { this.twelfthMarks = v; }
    public double getEntranceScore()              { return entranceScore; }
    public void setEntranceScore(double v)        { this.entranceScore = v; }
    public double getMeritScore()                 { return meritScore; }
    public void setMeritScore(double v)           { this.meritScore = v; }
    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime v)     { this.createdAt = v; }

    @Override
    public String toString() {
        return String.format("[%d] %s | Merit: %.2f", studentId, getFullName(), meritScore);
    }
}
