package com.sisregistration.bmwsis.service;

import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private SubjectScheduleRepository subjectScheduleRepository;
    
    @Autowired
    private EnrollmentPeriodRepository enrollmentPeriodRepository;
    
    @Autowired
    private CurriculumRepository curriculumRepository;
    
    @Autowired
    private CurriculumAssignmentRepository curriculumAssignmentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<Student> authenticateStudent(String studentId, String password) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            // Use BCrypt password matching
            if (passwordEncoder.matches(password, student.getPassword())) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }
    
    public List<Grade> getStudentGrades(Student student) {
        return gradeRepository.findByStudentOrderBySemesterAscAcademicYearAsc(student);
    }
    
    /**
     * Get student grades with comprehensive information
     */
    public List<Grade> getStudentGradesWithDetails(Student student) {
        return gradeRepository.findByStudentOrderBySemesterAscAcademicYearAsc(student);
    }
    
    /**
     * Get student grades for current semester
     */
    public List<Grade> getCurrentSemesterGrades(Student student) {
        String currentSemester = getCurrentSemester();
        String currentAcademicYear = getCurrentAcademicYear();
        return gradeRepository.findByStudentAndSemesterAndAcademicYear(student, currentSemester, currentAcademicYear);
    }
    
    /**
     * Get student grade statistics
     */
    public StudentGradeStatistics getStudentGradeStatistics(Student student) {
        List<Grade> allGrades = getStudentGrades(student);
        
        long totalSubjects = allGrades.size();
        long passedCount = allGrades.stream().filter(Grade::isPassed).count();
        long failedCount = allGrades.stream().filter(g -> "FAILED".equals(g.getStatus())).count();
        long pendingCount = allGrades.stream().filter(g -> "ONGOING".equals(g.getStatus())).count();
        
        double gpa = allGrades.stream()
            .filter(g -> g.getFinalRating() != null)
            .mapToDouble(Grade::getFinalRating)
            .average()
            .orElse(0.0);
        
        return new StudentGradeStatistics(totalSubjects, passedCount, failedCount, pendingCount, gpa);
    }
    
    private String getCurrentSemester() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        if (month >= 8 || month <= 1) {
            return "1st";
        } else {
            return "2nd";
        }
    }
    
    private String getCurrentAcademicYear() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        if (now.getMonthValue() >= 8) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
    
    /**
     * Student Grade Statistics DTO
     */
    public static class StudentGradeStatistics {
        private Long totalSubjects;
        private Long passedCount;
        private Long failedCount;
        private Long pendingCount;
        private Double gpa;
        
        public StudentGradeStatistics(Long totalSubjects, Long passedCount, Long failedCount, Long pendingCount, Double gpa) {
            this.totalSubjects = totalSubjects;
            this.passedCount = passedCount;
            this.failedCount = failedCount;
            this.pendingCount = pendingCount;
            this.gpa = gpa;
        }
        
        // Getters
        public Long getTotalSubjects() { return totalSubjects; }
        public Long getPassedCount() { return passedCount; }
        public Long getFailedCount() { return failedCount; }
        public Long getPendingCount() { return pendingCount; }
        public Double getGpa() { return gpa; }
        public Double getPassRate() { 
            return totalSubjects > 0 ? (passedCount.doubleValue() / totalSubjects.doubleValue()) * 100 : 0.0; 
        }
    }
    
    public List<Enrollment> getStudentSchedules(Student student) {
        return enrollmentRepository.findCurrentEnrollmentsByStudent(student);
    }
    
    /**
     * Get student schedules for current semester (including graded subjects)
     * This shows all subjects the student is enrolled in for the current semester,
     * even if they have been graded, as long as the semester hasn't ended.
     * 
     * Enhanced to handle legacy data formats and provide fallback options.
     * Updated to be more flexible in finding active enrollments.
     */
    public List<Enrollment> getCurrentSemesterSchedules(Student student) {
        String currentSemester = getCurrentSemester();
        String currentAcademicYear = getCurrentAcademicYear();
        
        // First try with exact current semester/year
        List<Enrollment> currentEnrollments = enrollmentRepository.findCurrentSemesterEnrollmentsByStudent(student, currentSemester, currentAcademicYear);
        
        // If no results, try alternative formats
        if (currentEnrollments.isEmpty()) {
            // Try with "Semester" suffix for legacy data
            String legacySemester = currentSemester + " Semester";
            currentEnrollments = enrollmentRepository.findCurrentSemesterEnrollmentsByStudent(student, legacySemester, currentAcademicYear);
            
            // If still no results, try with previous semester to show recently completed courses
            if (currentEnrollments.isEmpty()) {
                String previousSemester = getCurrentSemester().equals("1st") ? "2nd" : "1st";
                currentEnrollments = enrollmentRepository.findCurrentSemesterEnrollmentsByStudent(student, previousSemester, currentAcademicYear);
                
                // Also try previous semester with legacy format
                if (currentEnrollments.isEmpty()) {
                    String legacyPreviousSemester = previousSemester + " Semester";
                    currentEnrollments = enrollmentRepository.findCurrentSemesterEnrollmentsByStudent(student, legacyPreviousSemester, currentAcademicYear);
                }
            }
        }
        
        // If still no results, fall back to showing ALL active enrollments (status = 'ENROLLED')
        // This ensures students can see subjects they enrolled in regardless of semester/year mismatch
        if (currentEnrollments.isEmpty()) {
            System.out.println("DEBUG: No enrollments found for current semester/year, falling back to all active enrollments for student: " + student.getStudentId());
            currentEnrollments = enrollmentRepository.findCurrentEnrollmentsByStudent(student);
            if (!currentEnrollments.isEmpty()) {
                System.out.println("DEBUG: Found " + currentEnrollments.size() + " active enrollments using fallback method");
            }
        }
        
        return currentEnrollments;
    }
    
    public List<SubjectSchedule> getAvailableSchedulesForStudent(Student student) {
        String currentSemester = getCurrentSemester();
        String currentAcademicYear = getCurrentAcademicYear();
        
        // NEW APPROACH: Get subjects from curriculum assignments
        // Students see subjects from admin-assigned curriculum packages
        List<CurriculumAssignment> assignments = curriculumAssignmentRepository.findActiveByStudent(student);
        
        System.out.println("DEBUG: Getting available schedules for student: " + student.getStudentId());
        System.out.println("DEBUG: Current semester: " + currentSemester + ", Academic year: " + currentAcademicYear);
        System.out.println("DEBUG: Found " + assignments.size() + " curriculum assignments for student");
        
        if (assignments.isEmpty()) {
            // No curriculum packages assigned = no subjects available
            System.out.println("DEBUG: No curriculum assignments found - returning empty list");
            return new ArrayList<>();
        }
        
        // Get subjects the student is already enrolled in
        List<Enrollment> existingEnrollments = enrollmentRepository.findByStudent(student);
        java.util.Set<Long> enrolledSubjectIds = new java.util.HashSet<>();
        for (Enrollment enrollment : existingEnrollments) {
            enrolledSubjectIds.add(enrollment.getSubject().getId());
        }
        
        // Get all subjects from assigned curriculum packages
        List<SubjectSchedule> availableSchedules = new ArrayList<>();
        java.util.Set<Long> addedSubjectIds = new java.util.HashSet<>(); // Track added subjects to prevent duplicates
        
        for (CurriculumAssignment assignment : assignments) {
            Curriculum curriculum = assignment.getCurriculum();
            if (curriculum.getSubjects() != null) {
                for (Subject subject : curriculum.getSubjects()) {
                    // Only show subjects the student is NOT already enrolled in
                    if (!enrolledSubjectIds.contains(subject.getId()) && !addedSubjectIds.contains(subject.getId())) {
                        // Find schedules for this subject
                        List<SubjectSchedule> subjectSchedules = subjectScheduleRepository
                            .findSchedulesForSubjectByStudentCriteria(subject.getId(), student.getYearLevel(), currentSemester, currentAcademicYear);
                        
                        // Find the best schedule for the student's section
                        SubjectSchedule bestSchedule = null;
                        for (SubjectSchedule schedule : subjectSchedules) {
                            if (schedule.getSectionCode().equals(student.getSection())) {
                                bestSchedule = schedule;
                                break; // Exact section match - use this one
                            } else if (bestSchedule == null && schedule.getSectionCode().startsWith(student.getCourse().substring(0, Math.min(2, student.getCourse().length())))) {
                                bestSchedule = schedule; // Fallback to course-related section
                            }
                        }
                        
                        // Add only the best schedule for this subject
                        if (bestSchedule != null) {
                            availableSchedules.add(bestSchedule);
                            addedSubjectIds.add(subject.getId());
                        }
                    }
                }
            }
        }
        
        return availableSchedules;
    }
    
    /**
     * Get schedules from active curriculum packages for the student's course and year level
     */
    public List<SubjectSchedule> getSchedulesFromActiveCurriculums(Student student, String semester, String academicYear) {
        // Convert semester string to integer (handle "1st" -> 1, "2nd" -> 2)
        int semesterInt;
        try {
            if (semester.equals("1st")) {
                semesterInt = 1;
            } else if (semester.equals("2nd")) {
                semesterInt = 2;
            } else {
                semesterInt = Integer.parseInt(semester);
            }
        } catch (NumberFormatException e) {
            semesterInt = 1; // Default to 1st semester
        }
        
        // Get active curriculums for the student's course, year level, and current semester
        List<Curriculum> activeCurriculums = curriculumRepository.findActiveCurriculumsByStudentCriteria(
            student.getCourse(), 
            student.getYearLevel(), 
            semesterInt, 
            academicYear
        );
        
        List<SubjectSchedule> availableSchedules = new ArrayList<>();
        
        for (Curriculum curriculum : activeCurriculums) {
            if (curriculum.getSubjects() != null) {
                for (Subject subject : curriculum.getSubjects()) {
                    // Find schedules for this subject that the student can enroll in
                    List<SubjectSchedule> subjectSchedules = subjectScheduleRepository
                        .findSchedulesForSubjectByStudentCriteria(subject.getId(), student.getYearLevel(), semester, academicYear);
                    
                    // Filter out schedules the student is already enrolled in
                    for (SubjectSchedule schedule : subjectSchedules) {
                        Optional<Enrollment> existingEnrollment = enrollmentRepository
                            .findActiveEnrollmentByStudentAndSubject(student, schedule.getSubject());
                        if (existingEnrollment.isEmpty()) {
                            availableSchedules.add(schedule);
                        }
                    }
                }
            }
        }
        
        return availableSchedules;
    }
    
    /**
     * Get available schedules for a specific semester and academic year
     */
    public List<SubjectSchedule> getAvailableSchedulesForStudentBySemester(Student student, String semester, String academicYear) {
        return subjectScheduleRepository.findAvailableSchedulesForStudentBySemester(student.getYearLevel(), student, semester, academicYear);
    }
    
    public boolean isEnrollmentOpen() {
        Optional<EnrollmentPeriod> activePeriod = enrollmentPeriodRepository.findActiveEnrollmentPeriod();
        System.out.println("DEBUG: Checking enrollment status...");
        System.out.println("DEBUG: Active period found: " + activePeriod.isPresent());
        
        if (activePeriod.isPresent()) {
            EnrollmentPeriod period = activePeriod.get();
            boolean isOpen = period.isEnrollmentOpen();
            System.out.println("DEBUG: Period details - isActive: " + period.getIsActive() + 
                             ", startDate: " + period.getStartDate() + 
                             ", endDate: " + period.getEndDate() + 
                             ", current time: " + java.time.LocalDateTime.now());
            System.out.println("DEBUG: isEnrollmentOpen result: " + isOpen);
            return isOpen;
        }
        
        System.out.println("DEBUG: No active enrollment period found");
        return false;
    }
    
    @Transactional
    public boolean enrollStudent(Student student, Long scheduleId) {
        if (!isEnrollmentOpen()) {
            return false;
        }
        
        Optional<SubjectSchedule> scheduleOpt = subjectScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            return false;
        }
        
        SubjectSchedule schedule = scheduleOpt.get();
        
        // Check if student is already enrolled in this subject
        Optional<Enrollment> existingEnrollment = enrollmentRepository
            .findActiveEnrollmentByStudentAndSubject(student, schedule.getSubject());
        if (existingEnrollment.isPresent()) {
            return false; // Student already enrolled in this subject
        }
        
        // Check capacity more accurately by counting current enrollments from database
        Long currentEnrolledCount = enrollmentRepository.countEnrolledStudentsBySchedule(scheduleId);
        if (currentEnrolledCount >= schedule.getMaxSlots()) {
            return false; // Class is full
        }
        
        // Double-check to prevent race conditions - refresh the schedule
        schedule = subjectScheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null || !schedule.hasAvailableSlots()) {
            return false;
        }
        
        // Create enrollment with current semester and academic year
        String currentSemester = getCurrentSemester();
        String currentAcademicYear = getCurrentAcademicYear();
        Enrollment enrollment = new Enrollment(
            student, 
            schedule.getSubject(), 
            schedule,
            currentSemester,
            currentAcademicYear
        );
        
        enrollmentRepository.save(enrollment);
        
        // Update current enrolled count based on actual database count
        schedule.setCurrentEnrolled(Math.toIntExact(currentEnrolledCount) + 1);
        subjectScheduleRepository.save(schedule);
        
        return true;
    }
    
    public Student findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }
    
    // Method to validate enrollment constraints without actually enrolling
    public EnrollmentResult validateEnrollment(Student student, Long scheduleId) {
        if (!isEnrollmentOpen()) {
            return new EnrollmentResult(false, "Enrollment period is currently closed.");
        }
        
        Optional<SubjectSchedule> scheduleOpt = subjectScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            return new EnrollmentResult(false, "Subject schedule not found.");
        }
        
        SubjectSchedule schedule = scheduleOpt.get();
        
        // Check if student is already enrolled in this subject
        Optional<Enrollment> existingEnrollment = enrollmentRepository
            .findActiveEnrollmentByStudentAndSubject(student, schedule.getSubject());
        if (existingEnrollment.isPresent()) {
            return new EnrollmentResult(false, "You are already enrolled in this subject (" + 
                schedule.getSubject().getSubjectCode() + ").");
        }
        
        // Check capacity
        Long currentEnrolledCount = enrollmentRepository.countEnrolledStudentsBySchedule(scheduleId);
        if (currentEnrolledCount >= schedule.getMaxSlots()) {
            return new EnrollmentResult(false, "This class is already full. No available slots remaining.");
        }
        
        return new EnrollmentResult(true, "Enrollment validation passed.");
    }
    
    // Enhanced enrollment method that provides detailed result information
    @Transactional
    public EnrollmentResult enrollStudentWithDetails(Student student, Long scheduleId) {
        if (!isEnrollmentOpen()) {
            return new EnrollmentResult(false, "Enrollment period is currently closed.");
        }
        
        Optional<SubjectSchedule> scheduleOpt = subjectScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            return new EnrollmentResult(false, "Subject schedule not found.");
        }
        
        SubjectSchedule schedule = scheduleOpt.get();
        
        // Check if student is already enrolled in this subject
        Optional<Enrollment> existingEnrollment = enrollmentRepository
            .findActiveEnrollmentByStudentAndSubject(student, schedule.getSubject());
        if (existingEnrollment.isPresent()) {
            return new EnrollmentResult(false, "You are already enrolled in this subject (" + 
                schedule.getSubject().getSubjectCode() + ").");
        }
        
        // Check capacity more accurately by counting current enrollments from database
        Long currentEnrolledCount = enrollmentRepository.countEnrolledStudentsBySchedule(scheduleId);
        if (currentEnrolledCount >= schedule.getMaxSlots()) {
            return new EnrollmentResult(false, "This class is already full. No available slots remaining.");
        }
        
        // Double-check to prevent race conditions - refresh the schedule
        schedule = subjectScheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null || !schedule.hasAvailableSlots()) {
            return new EnrollmentResult(false, "Class became full while processing your enrollment. Please try another section.");
        }
        
        try {
            // Create enrollment with current semester and academic year
            String currentSemester = getCurrentSemester();
            String currentAcademicYear = getCurrentAcademicYear();
            Enrollment enrollment = new Enrollment(
                student, 
                schedule.getSubject(), 
                schedule,
                currentSemester,
                currentAcademicYear
            );
            
            enrollmentRepository.save(enrollment);
            
            // Update current enrolled count based on actual database count
            schedule.setCurrentEnrolled(Math.toIntExact(currentEnrolledCount) + 1);
            subjectScheduleRepository.save(schedule);
            
            return new EnrollmentResult(true, "Successfully enrolled in " + 
                schedule.getSubject().getSubjectCode() + " - " + schedule.getSectionCode() + ".");
                
        } catch (Exception e) {
            return new EnrollmentResult(false, "An error occurred while processing your enrollment. Please try again.");
        }
    }
    
    // Method to refresh enrollment counts for all schedules (useful for maintenance)
    @Transactional
    public void refreshAllScheduleEnrollmentCounts() {
        List<SubjectSchedule> allSchedules = subjectScheduleRepository.findAll();
        for (SubjectSchedule schedule : allSchedules) {
            Long actualCount = enrollmentRepository.countEnrolledStudentsBySchedule(schedule.getId());
            schedule.setCurrentEnrolled(Math.toIntExact(actualCount));
            subjectScheduleRepository.save(schedule);
        }
    }
    
    // Get real-time enrollment count for a specific schedule
    public Long getRealTimeEnrollmentCount(Long scheduleId) {
        return enrollmentRepository.countEnrolledStudentsBySchedule(scheduleId);
    }
    
    // Get available slots for a specific schedule in real-time
    public Integer getRealTimeAvailableSlots(SubjectSchedule schedule) {
        Long currentCount = enrollmentRepository.countEnrolledStudentsBySchedule(schedule.getId());
        return schedule.getMaxSlots() - Math.toIntExact(currentCount);
    }
    
    // Get slots display string for a specific schedule in real-time
    public String getRealTimeSlotsDisplay(SubjectSchedule schedule) {
        Long currentCount = enrollmentRepository.countEnrolledStudentsBySchedule(schedule.getId());
        Integer available = schedule.getMaxSlots() - Math.toIntExact(currentCount);
        return available + "/" + schedule.getMaxSlots();
    }
    
    // Check if schedule has available slots in real-time
    public boolean hasRealTimeAvailableSlots(SubjectSchedule schedule) {
        Long currentCount = enrollmentRepository.countEnrolledStudentsBySchedule(schedule.getId());
        return currentCount < schedule.getMaxSlots();
    }
    
    // Get all schedules (for admin/debug purposes)
    public List<SubjectSchedule> getAllSchedules() {
        return subjectScheduleRepository.findAll();
    }
    
    // Get available schedules with real-time slot data for display
    public List<ScheduleDisplayInfo> getAvailableSchedulesWithRealTimeData(Student student) {
        List<SubjectSchedule> schedules = getAvailableSchedulesForStudent(student); // Now uses semester filtering
        List<ScheduleDisplayInfo> displayInfoList = new ArrayList<>();
        
        for (SubjectSchedule schedule : schedules) {
            Long realTimeEnrolledCount = enrollmentRepository.countEnrolledStudentsBySchedule(schedule.getId());
            Integer availableSlots = schedule.getMaxSlots() - Math.toIntExact(realTimeEnrolledCount);
            boolean hasSlots = realTimeEnrolledCount < schedule.getMaxSlots();
            String slotsDisplay = realTimeEnrolledCount + "/" + schedule.getMaxSlots();
            
            ScheduleDisplayInfo displayInfo = new ScheduleDisplayInfo(schedule, availableSlots, hasSlots, slotsDisplay);
            displayInfoList.add(displayInfo);
        }
        
        return displayInfoList;
    }
    
    // Inner class for detailed enrollment results
    public static class EnrollmentResult {
        private final boolean success;
        private final String message;
        
        public EnrollmentResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    // DTO class for schedule display with real-time slot information
    public static class ScheduleDisplayInfo {
        private final SubjectSchedule schedule;
        private final Integer availableSlots;
        private final boolean hasAvailableSlots;
        private final String slotsDisplay;
        
        public ScheduleDisplayInfo(SubjectSchedule schedule, Integer availableSlots, boolean hasAvailableSlots, String slotsDisplay) {
            this.schedule = schedule;
            this.availableSlots = availableSlots;
            this.hasAvailableSlots = hasAvailableSlots;
            this.slotsDisplay = slotsDisplay;
        }
        
        public SubjectSchedule getSchedule() {
            return schedule;
        }
        
        public Integer getAvailableSlots() {
            return availableSlots;
        }
        
        public boolean isHasAvailableSlots() {
            return hasAvailableSlots;
        }
        
        public String getSlotsDisplay() {
            return slotsDisplay;
        }
    }
    
    /**
     * Utility method to normalize legacy semester data
     * This can be called to fix existing enrollments in the database
     */
    @Transactional
    public int normalizeLegacySemesterData() {
        // Find all enrollments with legacy semester format
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        int updatedCount = 0;
        
        for (Enrollment enrollment : allEnrollments) {
            String semester = enrollment.getSemester();
            if (semester != null) {
                String normalizedSemester = null;
                if (semester.equals("1st Semester")) {
                    normalizedSemester = "1st";
                } else if (semester.equals("2nd Semester")) {
                    normalizedSemester = "2nd";
                }
                
                if (normalizedSemester != null) {
                    enrollment.setSemester(normalizedSemester);
                    enrollmentRepository.save(enrollment);
                    updatedCount++;
                }
            }
        }
        
        return updatedCount;
    }
    
    /**
     * Update existing schedules with semester and academic year information
     * This can be called to fix existing schedules that don't have semester data
     */
    @Transactional
    public int updateSchedulesWithSemesterData() {
        List<SubjectSchedule> allSchedules = subjectScheduleRepository.findAll();
        int updatedCount = 0;
        
        // Current semester and academic year for defaults
        String currentSemester = getCurrentSemester();
        String currentAcademicYear = getCurrentAcademicYear();
        
        for (SubjectSchedule schedule : allSchedules) {
            boolean needsUpdate = false;
            
            if (schedule.getSemester() == null || schedule.getSemester().isEmpty()) {
                schedule.setSemester(currentSemester);
                needsUpdate = true;
            }
            
            if (schedule.getAcademicYear() == null || schedule.getAcademicYear().isEmpty()) {
                schedule.setAcademicYear(currentAcademicYear);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                subjectScheduleRepository.save(schedule);
                updatedCount++;
            }
        }
        
        return updatedCount;
    }
} 