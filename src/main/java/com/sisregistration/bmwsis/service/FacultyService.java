package com.sisregistration.bmwsis.service;

import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @Autowired
    private SubjectScheduleRepository scheduleRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public Optional<Faculty> authenticateFaculty(String facultyId, String password) {
        Optional<Faculty> facultyOpt = facultyRepository.findByFacultyId(facultyId);
        
        if (facultyOpt.isPresent()) {
            Faculty faculty = facultyOpt.get();
            // Simple password comparison - in production, use proper hashing
            if (password.equals(faculty.getPassword())) {
                return Optional.of(faculty);
            }
        }
        return Optional.empty();
    }
    
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }
    
    public List<SubjectSchedule> getFacultySchedules(Faculty faculty) {
        return scheduleRepository.findByFacultyId(faculty.getId());
    }
    
    public List<Enrollment> getStudentsInSchedule(Long scheduleId) {
        return enrollmentRepository.findByScheduleId(scheduleId);
    }
    
    public List<Grade> getFacultyGrades(Faculty faculty) {
        return gradeRepository.findByFacultyId(faculty.getId());
    }
    
    public List<Student> getFacultyStudents(Faculty faculty) {
        // Get all students connected to this faculty through enrollments and grades
        java.util.Set<Student> studentSet = new java.util.HashSet<>();
        
        System.out.println("=== DEBUG getFacultyStudents for " + faculty.getFacultyId() + " ===");
        System.out.println("Faculty entity ID: " + faculty.getId());
        System.out.println("Faculty ID: " + faculty.getFacultyId());
        
        // Method 1: Get students from current enrollments
        List<SubjectSchedule> facultySchedules = scheduleRepository.findByFacultyId(faculty.getId());
        System.out.println("Faculty schedules found: " + facultySchedules.size());
        
        for (SubjectSchedule schedule : facultySchedules) {
            System.out.println("Processing schedule ID: " + schedule.getId() + 
                             " Subject: " + (schedule.getSubject() != null ? schedule.getSubject().getSubjectCode() : "NULL") +
                             " Section: " + schedule.getSectionCode());
            
            List<Enrollment> enrollments = enrollmentRepository.findByScheduleId(schedule.getId());
            System.out.println("Schedule " + (schedule.getSubject() != null ? schedule.getSubject().getSubjectCode() : "NULL") + 
                             " has " + enrollments.size() + " enrollments");
            
            for (Enrollment enrollment : enrollments) {
                Student student = enrollment.getStudent();
                if (student != null) {
                System.out.println("  Found student via enrollment: " + student.getStudentId() + " - " + 
                                 student.getFirstName() + " " + student.getLastName() + 
                                 " (Status: " + enrollment.getStatus() + ")");
                
                // Include ALL students regardless of status to fix the Tom Riddle issue
                studentSet.add(student);
                } else {
                    System.out.println("  WARNING: Enrollment " + enrollment.getId() + " has null student!");
                }
            }
        }
        
        // Method 2: Get students from existing grade records (for historical data)
        List<Grade> facultyGrades = gradeRepository.findByFacultyId(faculty.getId());
        System.out.println("Faculty grades found: " + facultyGrades.size());
        
        for (Grade grade : facultyGrades) {
            Student student = grade.getStudent();
            if (student != null) {
            System.out.println("  Found student via grade: " + student.getStudentId() + " - " + 
                             student.getFirstName() + " " + student.getLastName());
            studentSet.add(student);
            } else {
                System.out.println("  WARNING: Grade " + grade.getId() + " has null student!");
            }
        }
        
        // Method 3: Alternative approach - directly query students through schedules if needed
        if (studentSet.isEmpty() || studentSet.size() < 2) {
            System.out.println("FALLBACK: Trying alternative query approach...");
            try {
                // Get all students connected through any enrollment to this faculty's schedules
                for (SubjectSchedule schedule : facultySchedules) {
                    List<Enrollment> allEnrollments = enrollmentRepository.findByScheduleId(schedule.getId());
                    for (Enrollment enrollment : allEnrollments) {
                        if (enrollment.getStudent() != null) {
                            studentSet.add(enrollment.getStudent());
                            System.out.println("  FALLBACK: Added student " + enrollment.getStudent().getStudentId());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in fallback approach: " + e.getMessage());
            }
        }
        
        // Convert to list and sort by last name
        List<Student> students = new java.util.ArrayList<>(studentSet);
        students.sort((s1, s2) -> {
            String name1 = s1.getLastName() + ", " + s1.getFirstName();
            String name2 = s2.getLastName() + ", " + s2.getFirstName();
            return name1.compareTo(name2);
        });
        
        System.out.println("Final student list size: " + students.size());
        for (Student student : students) {
            System.out.println("  Final list: " + student.getStudentId() + " - " + 
                             student.getFirstName() + " " + student.getLastName());
        }
        
        // Additional debugging: If we expect more students, let's check if there are orphaned records
        if (students.size() < 2) {
            System.out.println("DEBUGGING: Expected more students, checking for potential issues...");
            
            // Check if there are any students in the database at all
            List<Student> allStudents = studentRepository.findAll();
            System.out.println("Total students in database: " + allStudents.size());
            
            // Check if FAC001 has any schedules at all
            if (facultySchedules.isEmpty()) {
                System.out.println("WARNING: Faculty " + faculty.getFacultyId() + " has no schedules assigned!");
            }
        }
        
        System.out.println("=== END DEBUG ===");
        
        return students;
    }
    
    public void saveGrade(Grade grade) {
        gradeRepository.save(grade);
    }
    
    public Optional<SubjectSchedule> getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }
    
    public Optional<Student> getStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }
    
    public void updateGrade(Long gradeId, Double gradeValue, String remarks) {
        Optional<Grade> gradeOpt = gradeRepository.findById(gradeId);
        if (gradeOpt.isPresent()) {
            Grade grade = gradeOpt.get();
            grade.setFinalGrade(gradeValue);
            grade.setStatus(remarks);
            grade.calculateFinalRating();
            gradeRepository.save(grade);
        }
    }
    
    /**
     * COMPREHENSIVE GRADE MANAGEMENT SYSTEM
     * Connects Faculty ↔ Students through Enrollments and Grades
     */
    
    /**
     * Get all students that need to be graded by a faculty member
     * This creates grade records automatically if they don't exist
     */
    public List<Grade> getFacultyGradesWithAutoCreation(Faculty faculty) {
        try {
            // Get all current schedules for this faculty
            List<SubjectSchedule> facultySchedules = scheduleRepository.findByFacultyId(faculty.getId());
            List<Grade> allGrades = new java.util.ArrayList<>();
            
            String currentSemester = getCurrentSemester();
            String currentAcademicYear = getCurrentAcademicYear();
            
            for (SubjectSchedule schedule : facultySchedules) {
                // Get all enrolled students for this schedule
                List<Enrollment> enrollments = enrollmentRepository.findByScheduleId(schedule.getId());
                
                for (Enrollment enrollment : enrollments) {
                    if ("ENROLLED".equals(enrollment.getStatus())) {
                        // Check if grade record exists using the improved method
                        List<Grade> existingGrades = gradeRepository
                            .findByStudentAndSubjectAndFacultyAndSectionCodeAndSemesterAndAcademicYearOrderById(
                                enrollment.getStudent(),
                                enrollment.getSubject(),
                                faculty,
                                schedule.getSectionCode(),
                                currentSemester,
                                currentAcademicYear
                            );
                        
                        Grade grade;
                        if (!existingGrades.isEmpty()) {
                            // Use the first grade record (oldest ID) and delete any duplicates
                            grade = existingGrades.get(0);
                            
                            // Clean up duplicates if they exist
                            if (existingGrades.size() > 1) {
                                System.out.println("Found " + existingGrades.size() + " duplicate grades for " + 
                                    enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName() + 
                                    " in " + enrollment.getSubject().getSubjectCode() + ". Cleaning up duplicates...");
                                
                                for (int i = 1; i < existingGrades.size(); i++) {
                                    try {
                                        gradeRepository.delete(existingGrades.get(i));
                                        System.out.println("Deleted duplicate grade ID: " + existingGrades.get(i).getId());
                                    } catch (Exception e) {
                                        System.err.println("Error deleting duplicate grade: " + e.getMessage());
                                    }
                                }
                            }
                        } else {
                            // Check one more time to prevent race conditions
                            Long existingCount = gradeRepository.countByStudentAndSubjectAndFacultyAndSectionCodeAndSemesterAndAcademicYear(
                                enrollment.getStudent(),
                                enrollment.getSubject(),
                                faculty,
                                schedule.getSectionCode(),
                                currentSemester,
                                currentAcademicYear
                            );
                            
                            if (existingCount > 0) {
                                // If count > 0 but our query didn't find them, there might be a timing issue
                                // Retry the query
                                existingGrades = gradeRepository
                                    .findByStudentAndSubjectAndFacultyAndSectionCodeAndSemesterAndAcademicYearOrderById(
                                        enrollment.getStudent(),
                                        enrollment.getSubject(),
                                        faculty,
                                        schedule.getSectionCode(),
                                        currentSemester,
                                        currentAcademicYear
                                    );
                                if (!existingGrades.isEmpty()) {
                                    grade = existingGrades.get(0);
                                } else {
                                    continue; // Skip this one if we can't find it
                                }
                        } else {
                            try {
                                // Auto-create grade record
                                grade = new Grade(
                                    enrollment.getStudent(),
                                    enrollment.getSubject(),
                                    faculty,
                                    schedule.getSectionCode(),
                                    currentSemester,
                                    currentAcademicYear
                                );
                                grade = gradeRepository.save(grade);
                            } catch (Exception e) {
                                // If auto-creation fails due to foreign key constraints, skip this grade
                                System.err.println("Warning: Could not auto-create grade record for student " + 
                                    enrollment.getStudent().getStudentId() + ": " + e.getMessage());
                                continue;
                                }
                            }
                        }
                        allGrades.add(grade);
                    }
                }
            }
            
            // Remove any potential duplicates from the final list using a Set
            java.util.Set<Long> addedGradeIds = new java.util.HashSet<>();
            List<Grade> uniqueGrades = new java.util.ArrayList<>();
            
            for (Grade grade : allGrades) {
                if (!addedGradeIds.contains(grade.getId())) {
                    uniqueGrades.add(grade);
                    addedGradeIds.add(grade.getId());
                }
            }
            
            // Sort by student name for better organization
            uniqueGrades.sort((g1, g2) -> {
                String name1 = g1.getStudent().getLastName() + ", " + g1.getStudent().getFirstName();
                String name2 = g2.getStudent().getLastName() + ", " + g2.getStudent().getFirstName();
                return name1.compareTo(name2);
            });
            
            return uniqueGrades;
        } catch (Exception e) {
            // If there's any error in the process, return existing grades only
            System.err.println("Error in getFacultyGradesWithAutoCreation: " + e.getMessage());
            e.printStackTrace();
            return getFacultyGrades(faculty);
        }
    }
    
    /**
     * Update individual grade (midterm or final)
     */
    public void updateIndividualGrade(Long gradeId, Double midtermGrade, Double finalGrade) {
        Optional<Grade> gradeOpt = gradeRepository.findById(gradeId);
        if (gradeOpt.isPresent()) {
            Grade grade = gradeOpt.get();
            
            if (midtermGrade != null) {
                grade.setMidtermGrade(midtermGrade);
            }
            if (finalGrade != null) {
                grade.setFinalGrade(finalGrade);
            }
            
            // Auto-calculate final rating and status
            grade.calculateFinalRating();
            gradeRepository.save(grade);
        }
    }
    
    /**
     * Get grades filtered by subject
     */
    public List<Grade> getFacultyGradesBySubject(Faculty faculty, Long subjectId) {
        List<Grade> allGrades = getFacultyGradesWithAutoCreation(faculty);
        return allGrades.stream()
            .filter(grade -> grade.getSubject().getId().equals(subjectId))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get grades filtered by section
     */
    public List<Grade> getFacultyGradesBySection(Faculty faculty, String sectionCode) {
        return gradeRepository.findByFacultyAndSectionCodeOrderByStudentLastNameAsc(faculty, sectionCode);
    }
    
    /**
     * Get grade statistics for faculty dashboard
     */
    public GradeStatistics getFacultyGradeStatistics(Faculty faculty) {
        Long totalStudents = (long) getFacultyGradesWithAutoCreation(faculty).size();
        Long passedCount = gradeRepository.countPassedByFaculty(faculty);
        Long failedCount = gradeRepository.countFailedByFaculty(faculty);
        Long pendingCount = gradeRepository.countPendingByFaculty(faculty);
        Double averageGrade = gradeRepository.getAverageGradeByFaculty(faculty);
        
        return new GradeStatistics(totalStudents, passedCount, failedCount, pendingCount, averageGrade != null ? averageGrade : 0.0);
    }
    
    /**
     * Get all unique subjects taught by faculty (for filtering)
     */
    public List<Subject> getFacultySubjects(Faculty faculty) {
        List<SubjectSchedule> schedules = scheduleRepository.findByFacultyId(faculty.getId());
        return schedules.stream()
            .map(SubjectSchedule::getSubject)
            .distinct()
            .sorted((s1, s2) -> s1.getSubjectCode().compareTo(s2.getSubjectCode()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get all unique sections taught by faculty (for filtering)
     */
    public List<String> getFacultyUniqueSections(Faculty faculty) {
        List<SubjectSchedule> schedules = scheduleRepository.findByFacultyId(faculty.getId());
        return schedules.stream()
            .map(SubjectSchedule::getSectionCode)
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Batch update grades
     */
    public void batchUpdateGrades(List<GradeUpdateRequest> gradeUpdates) {
        for (GradeUpdateRequest update : gradeUpdates) {
            updateIndividualGrade(update.getGradeId(), update.getMidtermGrade(), update.getFinalGrade());
        }
    }
    
    private String getCurrentSemester() {
        // Logic to determine current semester (1st or 2nd)
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        if (month >= 8 || month <= 1) {
            return "1st";
        } else {
            return "2nd";
        }
    }
    
    private String getCurrentAcademicYear() {
        // Logic to determine academic year (e.g., 2024-2025)
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        if (now.getMonthValue() >= 8) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
    
    /**
     * Gets a comprehensive summary of faculty's section assignments
     */
    public FacultyAssignmentSummary getFacultyAssignmentSummary(Faculty faculty) {
        List<SubjectSchedule> schedules = getFacultySchedules(faculty);
        
        FacultyAssignmentSummary summary = new FacultyAssignmentSummary();
        summary.setFaculty(faculty);
        summary.setTotalSchedules(schedules.size());
        
        // Count unique sections and subjects
        java.util.Set<String> uniqueSections = new java.util.HashSet<>();
        java.util.Set<String> uniqueSubjects = new java.util.HashSet<>();
        int totalStudents = 0;
        
        for (SubjectSchedule schedule : schedules) {
            uniqueSections.add(schedule.getSectionCode());
            uniqueSubjects.add(schedule.getSubject().getSubjectCode());
            
            // Count enrolled students in this schedule
            List<Enrollment> enrollments = enrollmentRepository.findByScheduleId(schedule.getId());
            totalStudents += enrollments.size();
        }
        
        summary.setUniqueSections(uniqueSections.size());
        summary.setUniqueSubjects(uniqueSubjects.size());
        summary.setTotalStudents(totalStudents);
        summary.setSectionCodes(uniqueSections);
        summary.setSubjectCodes(uniqueSubjects);
        summary.setSchedules(schedules);
        
        return summary;
    }
    
    /**
     * Gets all sections that this faculty is assigned to teach
     */
    public List<String> getFacultySections(Faculty faculty) {
        List<SubjectSchedule> schedules = getFacultySchedules(faculty);
        return schedules.stream()
                       .map(SubjectSchedule::getSectionCode)
                       .distinct()
                       .sorted()
                       .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets schedule information grouped by section for better organization
     */
    public java.util.Map<String, List<SubjectSchedule>> getFacultySchedulesBySection(Faculty faculty) {
        List<SubjectSchedule> schedules = getFacultySchedules(faculty);
        return schedules.stream()
                       .collect(java.util.stream.Collectors.groupingBy(
                           SubjectSchedule::getSectionCode,
                           java.util.TreeMap::new,
                           java.util.stream.Collectors.toList()
                       ));
    }
    
    /**
     * Gets schedule information grouped by program/course for cross-program teaching
     */
    public java.util.Map<String, List<SubjectSchedule>> getFacultySchedulesByProgram(Faculty faculty) {
        List<SubjectSchedule> schedules = getFacultySchedules(faculty);
        return schedules.stream()
                       .collect(java.util.stream.Collectors.groupingBy(
                           schedule -> extractProgramFromSection(schedule.getSectionCode()),
                           java.util.TreeMap::new,
                           java.util.stream.Collectors.toList()
                       ));
    }
    
    /**
     * Gets schedule information grouped by day of week for daily organization
     */
    public java.util.Map<String, List<SubjectSchedule>> getFacultySchedulesByDay(Faculty faculty) {
        List<SubjectSchedule> schedules = getFacultySchedules(faculty);
        // Define day order for proper sorting
        java.util.Map<String, Integer> dayOrder = new java.util.HashMap<>();
        dayOrder.put("Monday", 1);
        dayOrder.put("Tuesday", 2);
        dayOrder.put("Wednesday", 3);
        dayOrder.put("Thursday", 4);
        dayOrder.put("Friday", 5);
        dayOrder.put("Saturday", 6);
        dayOrder.put("Sunday", 7);
        
        return schedules.stream()
                       .collect(java.util.stream.Collectors.groupingBy(
                           SubjectSchedule::getDayOfWeek,
                           () -> new java.util.TreeMap<>((a, b) -> 
                               dayOrder.getOrDefault(a, 8).compareTo(dayOrder.getOrDefault(b, 8))),
                           java.util.stream.Collectors.collectingAndThen(
                               java.util.stream.Collectors.toList(),
                               list -> list.stream()
                                          .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                                          .collect(java.util.stream.Collectors.toList())
                           )
                       ));
    }
    
    /**
     * Gets today's schedule for quick daily reference
     */
    public List<SubjectSchedule> getTodaySchedule(Faculty faculty) {
        String today = java.time.LocalDate.now().getDayOfWeek().getDisplayName(
            java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        
        return getFacultySchedules(faculty).stream()
                                          .filter(schedule -> schedule.getDayOfWeek().equals(today))
                                          .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                                          .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets next upcoming class for faculty
     */
    public Optional<SubjectSchedule> getNextClass(Faculty faculty) {
        String today = java.time.LocalDate.now().getDayOfWeek().getDisplayName(
            java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        java.time.LocalTime now = java.time.LocalTime.now();
        
        return getFacultySchedules(faculty).stream()
                                          .filter(schedule -> schedule.getDayOfWeek().equals(today))
                                          .filter(schedule -> schedule.getStartTime().isAfter(now))
                                          .min((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
    }
    
    /**
     * Extracts program name from section code (e.g., "IT-2A" -> "IT", "CS-3B" -> "CS")
     */
    private String extractProgramFromSection(String sectionCode) {
        if (sectionCode == null || sectionCode.isEmpty()) {
            return "Unknown";
        }
        
        // Extract program prefix (everything before the first dash or digit)
        String[] parts = sectionCode.split("-");
        if (parts.length > 0) {
            String program = parts[0].replaceAll("\\d", "").trim();
            
            // Map common abbreviations to full names for better display
            switch (program.toUpperCase()) {
                case "IT": return "Information Technology";
                case "CS": case "BSCS": return "Computer Science";
                case "IS": return "Information Systems";
                case "CE": return "Computer Engineering";
                case "MATH": return "Mathematics";
                case "ENG": return "Engineering";
                case "BUS": return "Business Administration";
                default: return program.toUpperCase();
            }
        }
        
        return "General";
    }
    
    /**
     * Gets teaching load summary across programs
     */
    public ProgramTeachingLoad getProgramTeachingLoad(Faculty faculty) {
        List<SubjectSchedule> schedules = getFacultySchedules(faculty);
        ProgramTeachingLoad load = new ProgramTeachingLoad();
        
        java.util.Map<String, Integer> programCounts = new java.util.HashMap<>();
        java.util.Map<String, java.util.Set<String>> programSections = new java.util.HashMap<>();
        
        for (SubjectSchedule schedule : schedules) {
            String program = extractProgramFromSection(schedule.getSectionCode());
            programCounts.put(program, programCounts.getOrDefault(program, 0) + 1);
            
            programSections.computeIfAbsent(program, k -> new java.util.HashSet<>())
                          .add(schedule.getSectionCode());
        }
        
        load.setProgramCounts(programCounts);
        load.setProgramSections(programSections);
        load.setTotalPrograms(programCounts.size());
        
        return load;
    }
    
    /**
     * Get comprehensive student statistics for faculty
     */
    public StudentStatistics getFacultyStudentStatistics(Faculty faculty) {
        List<Student> allStudents = getFacultyStudents(faculty);
        List<Grade> allGrades = gradeRepository.findByFacultyId(faculty.getId());
        
        long totalStudents = allStudents.size();
        long activeStudents = 0;
        long passingStudents = 0;
        double totalGradeSum = 0.0;
        int gradedStudentsCount = 0;
        
        // Calculate active students (those with current enrollments)
        List<SubjectSchedule> facultySchedules = scheduleRepository.findByFacultyId(faculty.getId());
        java.util.Set<Long> activeStudentIds = new java.util.HashSet<>();
        
        for (SubjectSchedule schedule : facultySchedules) {
            List<Enrollment> enrollments = enrollmentRepository.findByScheduleId(schedule.getId());
            for (Enrollment enrollment : enrollments) {
                if ("ENROLLED".equals(enrollment.getStatus())) {
                    activeStudentIds.add(enrollment.getStudent().getId());
                }
            }
        }
        activeStudents = activeStudentIds.size();
        
        // Calculate grade statistics
        for (Grade grade : allGrades) {
            if (grade.getFinalRating() != null && grade.getFinalRating() > 0) {
                totalGradeSum += grade.getFinalRating();
                gradedStudentsCount++;
                
                if (grade.getFinalRating() >= 75.0) {
                    passingStudents++;
                }
            }
        }
        
        double averageGrade = gradedStudentsCount > 0 ? totalGradeSum / gradedStudentsCount : 0.0;
        
        return new StudentStatistics(totalStudents, activeStudents, passingStudents, averageGrade);
    }
    
    /**
     * Grade Statistics DTO
     */
    public static class GradeStatistics {
        private Long totalStudents;
        private Long passedCount;
        private Long failedCount;
        private Long pendingCount;
        private Double averageGrade;
        
        public GradeStatistics(Long totalStudents, Long passedCount, Long failedCount, Long pendingCount, Double averageGrade) {
            this.totalStudents = totalStudents;
            this.passedCount = passedCount;
            this.failedCount = failedCount;
            this.pendingCount = pendingCount;
            this.averageGrade = averageGrade;
        }
        
        // Getters
        public Long getTotalStudents() { return totalStudents; }
        public Long getPassedCount() { return passedCount; }
        public Long getFailedCount() { return failedCount; }
        public Long getPendingCount() { return pendingCount; }
        public Double getAverageGrade() { return averageGrade; }
        public Double getPassRate() { 
            return totalStudents > 0 ? (passedCount.doubleValue() / totalStudents.doubleValue()) * 100 : 0.0; 
        }
    }
    
    /**
     * Grade Update Request DTO
     */
    public static class GradeUpdateRequest {
        private Long gradeId;
        private Double midtermGrade;
        private Double finalGrade;
        
        public GradeUpdateRequest() {}
        
        public GradeUpdateRequest(Long gradeId, Double midtermGrade, Double finalGrade) {
            this.gradeId = gradeId;
            this.midtermGrade = midtermGrade;
            this.finalGrade = finalGrade;
        }
        
        // Getters and Setters
        public Long getGradeId() { return gradeId; }
        public void setGradeId(Long gradeId) { this.gradeId = gradeId; }
        public Double getMidtermGrade() { return midtermGrade; }
        public void setMidtermGrade(Double midtermGrade) { this.midtermGrade = midtermGrade; }
        public Double getFinalGrade() { return finalGrade; }
        public void setFinalGrade(Double finalGrade) { this.finalGrade = finalGrade; }
    }
    
    /**
     * Inner class for program teaching load information
     */
    public static class ProgramTeachingLoad {
        private java.util.Map<String, Integer> programCounts = new java.util.HashMap<>();
        private java.util.Map<String, java.util.Set<String>> programSections = new java.util.HashMap<>();
        private int totalPrograms;
        
        // Getters and setters
        public java.util.Map<String, Integer> getProgramCounts() { return programCounts; }
        public void setProgramCounts(java.util.Map<String, Integer> programCounts) { this.programCounts = programCounts; }
        
        public java.util.Map<String, java.util.Set<String>> getProgramSections() { return programSections; }
        public void setProgramSections(java.util.Map<String, java.util.Set<String>> programSections) { this.programSections = programSections; }
        
        public int getTotalPrograms() { return totalPrograms; }
        public void setTotalPrograms(int totalPrograms) { this.totalPrograms = totalPrograms; }
        
        public String getProgramList() {
            return String.join(", ", programCounts.keySet());
        }
        
        public int getTotalClasses() {
            return programCounts.values().stream().mapToInt(Integer::intValue).sum();
        }
    }
    
    /**
     * Inner class to hold faculty assignment summary information
     */
    public static class FacultyAssignmentSummary {
        private Faculty faculty;
        private int totalSchedules;
        private int uniqueSections;
        private int uniqueSubjects;
        private int totalStudents;
        private java.util.Set<String> sectionCodes;
        private java.util.Set<String> subjectCodes;
        private List<SubjectSchedule> schedules;
        
        // Getters and setters
        public Faculty getFaculty() { return faculty; }
        public void setFaculty(Faculty faculty) { this.faculty = faculty; }
        
        public int getTotalSchedules() { return totalSchedules; }
        public void setTotalSchedules(int totalSchedules) { this.totalSchedules = totalSchedules; }
        
        public int getUniqueSections() { return uniqueSections; }
        public void setUniqueSections(int uniqueSections) { this.uniqueSections = uniqueSections; }
        
        public int getUniqueSubjects() { return uniqueSubjects; }
        public void setUniqueSubjects(int uniqueSubjects) { this.uniqueSubjects = uniqueSubjects; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public java.util.Set<String> getSectionCodes() { return sectionCodes; }
        public void setSectionCodes(java.util.Set<String> sectionCodes) { this.sectionCodes = sectionCodes; }
        
        public java.util.Set<String> getSubjectCodes() { return subjectCodes; }
        public void setSubjectCodes(java.util.Set<String> subjectCodes) { this.subjectCodes = subjectCodes; }
        
        public List<SubjectSchedule> getSchedules() { return schedules; }
        public void setSchedules(List<SubjectSchedule> schedules) { this.schedules = schedules; }
        
        public String getSectionList() {
            return sectionCodes != null ? String.join(", ", sectionCodes) : "";
        }
        
        public String getSubjectList() {
            return subjectCodes != null ? String.join(", ", subjectCodes) : "";
        }
    }
    
    /**
     * Student Statistics DTO for faculty students page
     */
    public static class StudentStatistics {
        private Long totalStudents;
        private Long activeStudents;
        private Long passingStudents;
        private Double averageGrade;
        
        public StudentStatistics(Long totalStudents, Long activeStudents, Long passingStudents, Double averageGrade) {
            this.totalStudents = totalStudents;
            this.activeStudents = activeStudents;
            this.passingStudents = passingStudents;
            this.averageGrade = averageGrade;
        }
        
        // Getters
        public Long getTotalStudents() { return totalStudents; }
        public Long getActiveStudents() { return activeStudents; }
        public Long getPassingStudents() { return passingStudents; }
        public Double getAverageGrade() { return averageGrade; }
        public Double getPassRate() { 
            return totalStudents > 0 ? (passingStudents.doubleValue() / totalStudents.doubleValue()) * 100 : 0.0; 
        }
    }
} 