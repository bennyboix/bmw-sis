package com.sisregistration.bmwsis.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonBackReference
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    @JsonBackReference
    private Faculty faculty;
    
    @Column(name = "section_code", nullable = false)
    private String sectionCode;
    
    @Column(name = "midterm_grade")
    private Double midtermGrade;
    
    @Column(name = "final_grade")
    private Double finalGrade;
    
    @Column(name = "final_rating")
    private Double finalRating;
    
    @Column(nullable = false)
    private String semester;
    
    @Column(nullable = false)
    private String academicYear;
    
    @Column(nullable = false)
    private String status; // PASSED, FAILED, INC, DRP
    
    // Constructors
    public Grade() {}
    
    public Grade(Student student, Subject subject, Faculty faculty, String sectionCode,
                String semester, String academicYear) {
        this.student = student;
        this.subject = subject;
        this.faculty = faculty;
        this.sectionCode = sectionCode;
        this.semester = semester;
        this.academicYear = academicYear;
        this.status = "ONGOING";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    
    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
    
    public Double getMidtermGrade() { return midtermGrade; }
    public void setMidtermGrade(Double midtermGrade) { this.midtermGrade = midtermGrade; }
    
    public Double getFinalGrade() { return finalGrade; }
    public void setFinalGrade(Double finalGrade) { this.finalGrade = finalGrade; }
    
    public Double getFinalRating() { return finalRating; }
    public void setFinalRating(Double finalRating) { this.finalRating = finalRating; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public void calculateFinalRating() {
        if (midtermGrade != null && finalGrade != null) {
            this.finalRating = (midtermGrade + finalGrade) / 2.0;
            this.status = finalRating >= 75.0 ? "PASSED" : "FAILED";
        }
    }
    
    public boolean isPassed() {
        return "PASSED".equals(status);
    }
}