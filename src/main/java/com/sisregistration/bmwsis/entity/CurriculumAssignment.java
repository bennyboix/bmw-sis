package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "curriculum_assignments")
public class CurriculumAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    @JsonBackReference
    private Curriculum curriculum;
    
    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;
    
    @Column(name = "assigned_by", nullable = false)
    private String assignedBy; // Admin ID or system
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, COMPLETED
    
    @Column(name = "semester", nullable = false)
    private String semester;
    
    @Column(name = "academic_year", nullable = false)
    private String academicYear;
    
    // Constructors
    public CurriculumAssignment() {}
    
    public CurriculumAssignment(Student student, Curriculum curriculum, String assignedBy, 
                               String semester, String academicYear) {
        this.student = student;
        this.curriculum = curriculum;
        this.assignedBy = assignedBy;
        this.assignedDate = LocalDateTime.now();
        this.semester = semester;
        this.academicYear = academicYear;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Curriculum getCurriculum() {
        return curriculum;
    }
    
    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }
    
    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }
    
    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }
    
    public String getAssignedBy() {
        return assignedBy;
    }
    
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    @Override
    public String toString() {
        return "CurriculumAssignment{" +
                "id=" + id +
                ", student=" + (student != null ? student.getStudentId() : null) +
                ", curriculum=" + (curriculum != null ? curriculum.getCurriculumCode() : null) +
                ", assignedDate=" + assignedDate +
                ", status='" + status + '\'' +
                '}';
    }
} 