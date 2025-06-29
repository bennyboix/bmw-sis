package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String studentId;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String course;
    
    @Column(nullable = false)
    private Integer yearLevel;
    
    @Column(nullable = false)
    private String section; // IT-1A, IT-1B, IT-2A, IT-2B, etc.
    
    private String phoneNumber;
    
    private String dateOfBirth;
    
    private String address;
    
    private String gender;
    
    // Alias for program (same as course)
    public String getProgram() { return course; }
    public void setProgram(String program) { this.course = program; }
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent circular reference in JSON serialization
    private List<Enrollment> enrollments;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent circular reference in JSON serialization
    private List<Grade> grades;
    
    // Constructors
    public Student() {}
    
    public Student(String studentId, String password, String firstName, String lastName, 
                   String email, String course, Integer yearLevel, String section) {
        this.studentId = studentId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.course = course;
        this.yearLevel = yearLevel;
        this.section = section;
    }
    
    public Student(String studentId, String password, String firstName, String lastName, 
                   String email, String course, Integer yearLevel, String section,
                   String phoneNumber, String dateOfBirth, String address, String gender) {
        this.studentId = studentId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.course = course;
        this.yearLevel = yearLevel;
        this.section = section;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }
    
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getFullSection() {
        return section; // Assuming section is already in format "IT-1A", "IT-1B", etc.
    }
    
    public String getDisplayName() {
        return getFullName() + " (" + getFullSection() + ")"; // e.g., "Mike Johnson (IT-1A)"
    }
}