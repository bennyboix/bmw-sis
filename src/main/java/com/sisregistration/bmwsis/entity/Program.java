package com.sisregistration.bmwsis.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "programs")
public class Program {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "program_code", unique = true, nullable = false)
    private String programCode; // BSIT, BSCS, BSCpE, etc.
    
    @Column(name = "program_name", nullable = false)
    private String programName; // Bachelor of Science in Information Technology, etc.
    
    @Column(name = "program_description")
    private String programDescription;
    
    @Column(name = "department", nullable = false)
    private String department; // Computer Science, Engineering, etc.
    
    @Column(name = "degree_level", nullable = false)
    private String degreeLevel = "Bachelor"; // Bachelor, Master, Doctorate
    
    @Column(name = "total_years", nullable = false)
    private Integer totalYears = 4; // Default 4 years
    
    @Column(name = "total_units", nullable = false)
    private Integer totalUnits = 120; // Default total units for the entire program
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED
    
    @Column(name = "established_date", nullable = false)
    private LocalDate establishedDate;
    
    @Column(name = "accreditation_status")
    private String accreditationStatus; // Accredited, Pending, Not Accredited
    
    @Column(name = "admission_requirements")
    private String admissionRequirements; // Text field for requirements
    
    @Column(name = "career_opportunities")
    private String careerOpportunities; // Text field for career paths
    
    // Note: Relationships will be added later when the corresponding foreign keys are added to other entities
    // For now, we'll use utility methods to get related data
    
    // Constructors
    public Program() {}
    
    public Program(String programCode, String programName, String department) {
        this.programCode = programCode;
        this.programName = programName;
        this.department = department;
        this.establishedDate = LocalDate.now();
    }
    
    public Program(String programCode, String programName, String programDescription, 
                   String department, Integer totalYears, Integer totalUnits) {
        this.programCode = programCode;
        this.programName = programName;
        this.programDescription = programDescription;
        this.department = department;
        this.totalYears = totalYears;
        this.totalUnits = totalUnits;
        this.establishedDate = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProgramCode() { return programCode; }
    public void setProgramCode(String programCode) { this.programCode = programCode; }
    
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
    
    public String getProgramDescription() { return programDescription; }
    public void setProgramDescription(String programDescription) { this.programDescription = programDescription; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDegreeLevel() { return degreeLevel; }
    public void setDegreeLevel(String degreeLevel) { this.degreeLevel = degreeLevel; }
    
    public Integer getTotalYears() { return totalYears; }
    public void setTotalYears(Integer totalYears) { this.totalYears = totalYears; }
    
    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDate getEstablishedDate() { return establishedDate; }
    public void setEstablishedDate(LocalDate establishedDate) { this.establishedDate = establishedDate; }
    
    public String getAccreditationStatus() { return accreditationStatus; }
    public void setAccreditationStatus(String accreditationStatus) { this.accreditationStatus = accreditationStatus; }
    
    public String getAdmissionRequirements() { return admissionRequirements; }
    public void setAdmissionRequirements(String admissionRequirements) { this.admissionRequirements = admissionRequirements; }
    
    public String getCareerOpportunities() { return careerOpportunities; }
    public void setCareerOpportunities(String careerOpportunities) { this.careerOpportunities = careerOpportunities; }
    
    // Utility methods
    public String getDisplayName() {
        return programCode + " - " + programName;
    }
    
    public String getStatusDisplay() {
        switch (status != null ? status.toUpperCase() : "ACTIVE") {
            case "ACTIVE": return "🟢 Active";
            case "INACTIVE": return "🔴 Inactive";
            case "SUSPENDED": return "🟡 Suspended";
            default: return "❓ Unknown";
        }
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    

    
    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", programCode='" + programCode + '\'' +
                ", programName='" + programName + '\'' +
                ", department='" + department + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
} 