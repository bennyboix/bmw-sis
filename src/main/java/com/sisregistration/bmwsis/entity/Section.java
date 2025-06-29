package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sections")
public class Section {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "section_code", unique = true, nullable = false)
    private String sectionCode; // IT-1A, IT-1B, CS-2A, etc.
    
    @Column(name = "section_name", nullable = false)
    private String sectionName;
    
    @Column(name = "course", nullable = false)
    private String course; // IT, CS, etc.
    
    @Column(name = "year_level", nullable = false)
    private Integer yearLevel;
    
    @Column(name = "academic_year", nullable = false)
    private String academicYear;
    
    @Column(name = "semester", nullable = false)
    private Integer semester;
    
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;
    
    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment = 0;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, CLOSED
    
    @Column(name = "room_assignment")
    private String roomAssignment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adviser_id")
    private Faculty adviser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;
    
    // Constructors
    public Section() {}
    
    public Section(String sectionCode, String sectionName, String course, 
                  Integer yearLevel, String academicYear, Integer semester, 
                  Integer maxCapacity) {
        this.sectionCode = sectionCode;
        this.sectionName = sectionName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.academicYear = academicYear;
        this.semester = semester;
        this.maxCapacity = maxCapacity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
    
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
    
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    
    public Integer getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(Integer currentEnrollment) { this.currentEnrollment = currentEnrollment; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRoomAssignment() { return roomAssignment; }
    public void setRoomAssignment(String roomAssignment) { this.roomAssignment = roomAssignment; }
    
    public Faculty getAdviser() { return adviser; }
    public void setAdviser(Faculty adviser) { this.adviser = adviser; }
    
    public Curriculum getCurriculum() { return curriculum; }
    public void setCurriculum(Curriculum curriculum) { this.curriculum = curriculum; }
    
    public boolean isAvailable() {
        return "ACTIVE".equals(status) && currentEnrollment < maxCapacity;
    }
    
    public int getAvailableSlots() {
        return maxCapacity - currentEnrollment;
    }
    
    @Override
    public String toString() {
        return sectionCode + " - " + sectionName + " (" + currentEnrollment + "/" + maxCapacity + ")";
    }
} 