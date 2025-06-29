package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_periods")
public class EnrollmentPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String semester;
    
    @Column(nullable = false)
    private String academicYear;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    // Constructors
    public EnrollmentPeriod() {}
    
    public EnrollmentPeriod(String semester, String academicYear, LocalDateTime startDate, 
                           LocalDateTime endDate, Boolean isActive) {
        this.semester = semester;
        this.academicYear = academicYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public boolean isEnrollmentOpen() {
        LocalDateTime now = LocalDateTime.now();
        // Use isAfter/isEqual for start date and isBefore/isEqual for end date to be more inclusive
        return isActive && 
               (now.isAfter(startDate) || now.isEqual(startDate)) && 
               (now.isBefore(endDate) || now.isEqual(endDate));
    }
} 