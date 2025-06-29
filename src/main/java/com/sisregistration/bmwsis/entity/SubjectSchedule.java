package com.sisregistration.bmwsis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "subject_schedules")
public class SubjectSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;
    
    @Column(nullable = false)
    private String sectionCode;
    
    @Column(nullable = false)
    private String dayOfWeek;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    @Column(nullable = false)
    private String room;
    
    @Column(nullable = false)
    private Integer maxSlots;
    
    @Column(nullable = false)
    private Integer currentEnrolled = 0;
    
    @Column(nullable = false)
    private String semester; // "1st" or "2nd"
    
    @Column(nullable = false)
    private String academicYear; // "2024-2025"
    
    @OneToMany(mappedBy = "subjectSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Prevent circular reference in JSON serialization
    private List<Enrollment> enrollments;
    
    // Constructors
    public SubjectSchedule() {}
    
    public SubjectSchedule(Subject subject, Faculty faculty, String sectionCode, String dayOfWeek,
                          LocalTime startTime, LocalTime endTime, String room, Integer maxSlots) {
        this.subject = subject;
        this.faculty = faculty;
        this.sectionCode = sectionCode;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.maxSlots = maxSlots;
        // Set default semester and academic year
        this.semester = getCurrentSemester();
        this.academicYear = getCurrentAcademicYear();
    }
    
    public SubjectSchedule(Subject subject, Faculty faculty, String sectionCode, String dayOfWeek,
                          LocalTime startTime, LocalTime endTime, String room, Integer maxSlots,
                          String semester, String academicYear) {
        this.subject = subject;
        this.faculty = faculty;
        this.sectionCode = sectionCode;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.maxSlots = maxSlots;
        this.semester = semester;
        this.academicYear = academicYear;
    }
    
    // Helper method to get current semester
    private String getCurrentSemester() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        return (month >= 8 || month <= 1) ? "1st" : "2nd";
    }
    
    // Helper method to get current academic year
    private String getCurrentAcademicYear() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        return (now.getMonthValue() >= 8) ? year + "-" + (year + 1) : (year - 1) + "-" + year;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    
    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
    
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    
    public Integer getMaxSlots() { return maxSlots; }
    public void setMaxSlots(Integer maxSlots) { this.maxSlots = maxSlots; }
    
    public Integer getCurrentEnrolled() { return currentEnrolled; }
    public void setCurrentEnrolled(Integer currentEnrolled) { this.currentEnrolled = currentEnrolled; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public boolean hasAvailableSlots() {
        return currentEnrolled < maxSlots;
    }
    
    public Integer getAvailableSlots() {
        return maxSlots - currentEnrolled;
    }
    
    public String getSlotsDisplay() {
        return getAvailableSlots() + "/" + maxSlots;
    }
    
    public String getTimeRange() {
        return startTime + " - " + endTime;
    }
    
    public String getScheduleInfo() {
        return sectionCode + " | " + dayOfWeek + " " + getTimeRange() + " | " + room;
    }
    
    @Override
    public String toString() {
        return "SubjectSchedule{" +
                "id=" + id +
                ", sectionCode='" + sectionCode + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", room='" + room + '\'' +
                ", maxSlots=" + maxSlots +
                ", currentEnrolled=" + currentEnrolled +
                ", subject=" + (subject != null ? subject.getSubjectCode() : "null") +
                ", faculty=" + (faculty != null ? faculty.getFullName() : "null") +
                '}';
    }
}