package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "system_settings")
public class SystemSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "system_name")
    private String systemName;
    
    @Column(name = "institution_name")
    private String institutionName;
    
    @Column(name = "academic_year")
    private String academicYear;
    
    @Column(name = "current_semester")
    private String currentSemester;
    
    @Column(name = "enrollment_open")
    private Boolean enrollmentOpen;
    
    @Column(name = "enrollment_start_date")
    private LocalDate enrollmentStartDate;
    
    @Column(name = "enrollment_end_date")
    private LocalDate enrollmentEndDate;
    
    @Column(name = "max_units_per_student")
    private Integer maxUnitsPerStudent;
    
    @Column(name = "passing_grade")
    private Integer passingGrade;
    
    @Column(name = "midterm_weight")
    private Integer midtermWeight;
    
    @Column(name = "final_weight")
    private Integer finalWeight;
    
    @Column(name = "grade_scale")
    private String gradeScale;
    
    @Column(name = "email_notifications")
    private Boolean emailNotifications;
    
    @Column(name = "sms_notifications")
    private Boolean smsNotifications;
    
    @Column(name = "enrollment_notifications")
    private Boolean enrollmentNotifications;
    
    @Column(name = "grade_notifications")
    private Boolean gradeNotifications;
    
    @Column(name = "session_timeout")
    private Integer sessionTimeout;
    
    @Column(name = "password_min_length")
    private Integer passwordMinLength;
    
    @Column(name = "require_password_change")
    private Boolean requirePasswordChange;
    
    @Column(name = "two_factor_auth")
    private Boolean twoFactorAuth;
    
    // Constructors
    public SystemSettings() {}
    
    public SystemSettings(String systemName, String institutionName, String academicYear, String currentSemester) {
        this.systemName = systemName;
        this.institutionName = institutionName;
        this.academicYear = academicYear;
        this.currentSemester = currentSemester;
        
        // Set default values
        this.enrollmentOpen = true;
        this.maxUnitsPerStudent = 24;
        this.passingGrade = 75;
        this.midtermWeight = 40;
        this.finalWeight = 60;
        this.gradeScale = "A-F";
        this.emailNotifications = true;
        this.smsNotifications = false;
        this.enrollmentNotifications = true;
        this.gradeNotifications = true;
        this.sessionTimeout = 60;
        this.passwordMinLength = 8;
        this.requirePasswordChange = true;
        this.twoFactorAuth = false;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public String getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }
    
    public Boolean getEnrollmentOpen() { return enrollmentOpen; }
    public void setEnrollmentOpen(Boolean enrollmentOpen) { this.enrollmentOpen = enrollmentOpen; }
    
    public LocalDate getEnrollmentStartDate() { return enrollmentStartDate; }
    public void setEnrollmentStartDate(LocalDate enrollmentStartDate) { this.enrollmentStartDate = enrollmentStartDate; }
    
    public LocalDate getEnrollmentEndDate() { return enrollmentEndDate; }
    public void setEnrollmentEndDate(LocalDate enrollmentEndDate) { this.enrollmentEndDate = enrollmentEndDate; }
    
    public Integer getMaxUnitsPerStudent() { return maxUnitsPerStudent; }
    public void setMaxUnitsPerStudent(Integer maxUnitsPerStudent) { this.maxUnitsPerStudent = maxUnitsPerStudent; }
    
    public Integer getPassingGrade() { return passingGrade; }
    public void setPassingGrade(Integer passingGrade) { this.passingGrade = passingGrade; }
    
    public Integer getMidtermWeight() { return midtermWeight; }
    public void setMidtermWeight(Integer midtermWeight) { this.midtermWeight = midtermWeight; }
    
    public Integer getFinalWeight() { return finalWeight; }
    public void setFinalWeight(Integer finalWeight) { this.finalWeight = finalWeight; }
    
    public String getGradeScale() { return gradeScale; }
    public void setGradeScale(String gradeScale) { this.gradeScale = gradeScale; }
    
    public Boolean getEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(Boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    
    public Boolean getSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(Boolean smsNotifications) { this.smsNotifications = smsNotifications; }
    
    public Boolean getEnrollmentNotifications() { return enrollmentNotifications; }
    public void setEnrollmentNotifications(Boolean enrollmentNotifications) { this.enrollmentNotifications = enrollmentNotifications; }
    
    public Boolean getGradeNotifications() { return gradeNotifications; }
    public void setGradeNotifications(Boolean gradeNotifications) { this.gradeNotifications = gradeNotifications; }
    
    public Integer getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(Integer sessionTimeout) { this.sessionTimeout = sessionTimeout; }
    
    public Integer getPasswordMinLength() { return passwordMinLength; }
    public void setPasswordMinLength(Integer passwordMinLength) { this.passwordMinLength = passwordMinLength; }
    
    public Boolean getRequirePasswordChange() { return requirePasswordChange; }
    public void setRequirePasswordChange(Boolean requirePasswordChange) { this.requirePasswordChange = requirePasswordChange; }
    
    public Boolean getTwoFactorAuth() { return twoFactorAuth; }
    public void setTwoFactorAuth(Boolean twoFactorAuth) { this.twoFactorAuth = twoFactorAuth; }
} 