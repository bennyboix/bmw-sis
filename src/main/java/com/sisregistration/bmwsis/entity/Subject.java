package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String subjectCode;
    
    @Column(nullable = false)
    private String subjectDescription;
    
    @Column(nullable = false)
    private Integer units;
    
    @Column(nullable = false)
    private String prerequisite;
    
    @Column(nullable = false)
    private Integer yearLevel;
    
    @Column(nullable = false)
    private Integer semester;
    
    @Column(nullable = false)
    private String course;
    
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent circular reference in JSON serialization
    private List<SubjectSchedule> schedules;
    
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent circular reference in JSON serialization
    private List<Enrollment> enrollments;
    
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent circular reference in JSON serialization
    private List<Grade> grades;
    
    // Constructors
    public Subject() {}
    
    public Subject(String subjectCode, String subjectDescription, Integer units, 
                   String prerequisite, Integer yearLevel, Integer semester, String course) {
        this.subjectCode = subjectCode;
        this.subjectDescription = subjectDescription;
        this.units = units;
        this.prerequisite = prerequisite;
        this.yearLevel = yearLevel;
        this.semester = semester;
        this.course = course;
    }
    
    // Backward compatibility constructor
    public Subject(String subjectCode, String subjectDescription, Integer units, 
                   String prerequisite, Integer yearLevel) {
        this.subjectCode = subjectCode;
        this.subjectDescription = subjectDescription;
        this.units = units;
        this.prerequisite = prerequisite;
        this.yearLevel = yearLevel;
        this.semester = 1; // Default to 1st semester
        this.course = "General"; // Default course
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    
    public String getSubjectDescription() { return subjectDescription; }
    public void setSubjectDescription(String subjectDescription) { this.subjectDescription = subjectDescription; }
    
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
    
    public String getPrerequisite() { return prerequisite; }
    public void setPrerequisite(String prerequisite) { this.prerequisite = prerequisite; }
    
    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }
    
    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    // Convenience methods for form binding compatibility
    public String getSubjectName() { return subjectDescription; }
    public void setSubjectName(String subjectName) { this.subjectDescription = subjectName; }
    
    public String getPrerequisites() { return prerequisite; }
    public void setPrerequisites(String prerequisites) { this.prerequisite = prerequisites; }
    
    public String getDescription() { return subjectDescription; }
    public void setDescription(String description) { this.subjectDescription = description; }
    
    public List<SubjectSchedule> getSchedules() { return schedules; }
    public void setSchedules(List<SubjectSchedule> schedules) { this.schedules = schedules; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
}