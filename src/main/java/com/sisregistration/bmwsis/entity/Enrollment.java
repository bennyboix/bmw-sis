package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_schedule_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private SubjectSchedule subjectSchedule;
    
    @Column(nullable = false)
    private LocalDateTime enrollmentDate;
    
    @Column(nullable = false)
    private String status; // ENROLLED, DROPPED, COMPLETED
    
    @Column(nullable = false)
    private String semester;
    
    @Column(nullable = false)
    private String academicYear;
    
    // Constructors
    public Enrollment() {}
    
    public Enrollment(Student student, Subject subject, SubjectSchedule subjectSchedule,
                     String semester, String academicYear) {
        this.student = student;
        this.subject = subject;
        this.subjectSchedule = subjectSchedule;
        this.enrollmentDate = LocalDateTime.now();
        this.status = "ENROLLED";
        this.semester = semester;
        this.academicYear = academicYear;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    
    public SubjectSchedule getSubjectSchedule() { return subjectSchedule; }
    public void setSubjectSchedule(SubjectSchedule subjectSchedule) { this.subjectSchedule = subjectSchedule; }
    
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
}