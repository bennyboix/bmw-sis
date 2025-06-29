package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "curriculum")
public class Curriculum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "curriculum_code", unique = true, nullable = false)
    private String curriculumCode;
    
    @Column(name = "curriculum_name", nullable = false)
    private String curriculumName;
    
    @Column(name = "course", nullable = false)
    private String course; // IT, CS, etc.
    
    @Column(name = "year_level", nullable = false)
    private Integer yearLevel;
    
    @Column(name = "semester", nullable = false)
    private Integer semester;
    
    @Column(name = "academic_year", nullable = false)
    private String academicYear;
    
    @Column(name = "total_units", nullable = false)
    private Integer totalUnits;
    
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DRAFT
    
    @Column(name = "description")
    private String description;
    
    // Many-to-Many relationship with subjects
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "curriculum_subjects",
        joinColumns = @JoinColumn(name = "curriculum_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects;
    
    // Constructors
    public Curriculum() {}
    
    public Curriculum(String curriculumCode, String curriculumName, String course, 
                     Integer yearLevel, Integer semester, String academicYear, 
                     Integer totalUnits, LocalDate effectiveDate) {
        this.curriculumCode = curriculumCode;
        this.curriculumName = curriculumName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.semester = semester;
        this.academicYear = academicYear;
        this.totalUnits = totalUnits;
        this.effectiveDate = effectiveDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCurriculumCode() { return curriculumCode; }
    public void setCurriculumCode(String curriculumCode) { this.curriculumCode = curriculumCode; }
    
    public String getCurriculumName() { return curriculumName; }
    public void setCurriculumName(String curriculumName) { this.curriculumName = curriculumName; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }
    
    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }
    
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }
    
    @Override
    public String toString() {
        return curriculumCode + " - " + curriculumName + " (" + course + " Year " + yearLevel + ")";
    }
} 