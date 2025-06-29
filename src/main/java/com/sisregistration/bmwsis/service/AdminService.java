package com.sisregistration.bmwsis.service;

import com.sisregistration.bmwsis.entity.*;
import com.sisregistration.bmwsis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.ArrayList;
import com.sisregistration.bmwsis.entity.EnrollmentPeriod;
import com.sisregistration.bmwsis.repository.EnrollmentPeriodRepository;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SubjectScheduleRepository scheduleRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private CurriculumRepository curriculumRepository;
    
    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private CurriculumAssignmentRepository curriculumAssignmentRepository;
    
    @Autowired
    private ProgramRepository programRepository;
    
    @Autowired
    private EnrollmentPeriodRepository enrollmentPeriodRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<Admin> authenticateAdmin(String adminId, String password) {
        Optional<Admin> adminOpt = adminRepository.findByAdminId(adminId);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // Use BCrypt password matching
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }
    
    // Student management
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public void addStudent(Student student) {
        try {
            // Validate required fields
            if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
                throw new RuntimeException("Student ID is required");
            }
            if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
                throw new RuntimeException("First name is required");
            }
            if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
                throw new RuntimeException("Last name is required");
            }
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (student.getCourse() == null || student.getCourse().trim().isEmpty()) {
                throw new RuntimeException("Course/Program is required");
            }
            if (student.getYearLevel() == null) {
                throw new RuntimeException("Year level is required");
            }
            if (student.getSection() == null || student.getSection().trim().isEmpty()) {
                throw new RuntimeException("Section is required");
            }
            if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            
            // Check if student ID already exists
            if (studentRepository.existsByStudentId(student.getStudentId())) {
                throw new RuntimeException("Student ID '" + student.getStudentId() + "' already exists");
            }
            
            // Check if section exists and has capacity
            if (student.getSection() != null && !student.getSection().trim().isEmpty()) {
                Optional<Section> sectionOpt = sectionRepository.findBySectionCode(student.getSection());
                if (sectionOpt.isPresent()) {
                    Section section = sectionOpt.get();
                    if (section.getCurrentEnrollment() >= section.getMaxCapacity()) {
                        throw new RuntimeException("Section " + student.getSection() + " is at full capacity (" + 
                                                 section.getCurrentEnrollment() + "/" + section.getMaxCapacity() + ")");
                    }
                } else {
                    throw new RuntimeException("Section '" + student.getSection() + "' not found. Please select a valid section.");
                }
            }
            
            // Save the student
            studentRepository.save(student);
            
            // Update section enrollment count
            if (student.getSection() != null && !student.getSection().trim().isEmpty()) {
                updateSectionEnrollment(student.getSection(), 1);
            }
            
        } catch (Exception e) {
            // Log the error
            System.err.println("Error adding student: " + e.getMessage());
            throw new RuntimeException("Failed to add student: " + e.getMessage(), e);
        }
    }
    
    public void updateStudent(Student student) {
        // Get the original student to check if section changed and preserve password
        Optional<Student> originalStudentOpt = studentRepository.findById(student.getId());
        if (originalStudentOpt.isPresent()) {
            Student originalStudent = originalStudentOpt.get();
            String oldSection = originalStudent.getSection();
            String newSection = student.getSection();
            
            // Preserve the original password if new password is null or empty
            if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
                student.setPassword(originalStudent.getPassword());
            }
            
            // If section changed, update enrollment counts
            if (!Objects.equals(oldSection, newSection)) {
                // Check if new section has capacity
                if (newSection != null) {
                    Optional<Section> newSectionOpt = sectionRepository.findBySectionCode(newSection);
                    if (newSectionOpt.isPresent()) {
                        Section section = newSectionOpt.get();
                        if (section.getCurrentEnrollment() >= section.getMaxCapacity()) {
                            throw new RuntimeException("Section " + newSection + " is at full capacity (" + 
                                                     section.getCurrentEnrollment() + "/" + section.getMaxCapacity() + ")");
                        }
                    }
                }
                
                // Decrease enrollment in old section
                if (oldSection != null) {
                    updateSectionEnrollment(oldSection, -1);
                }
                
                // Increase enrollment in new section
                if (newSection != null) {
                    updateSectionEnrollment(newSection, 1);
                }
            }
        } else {
            // If original student not found and password is empty, set a default
            if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
                student.setPassword("student123");
            }
        }
        
        studentRepository.save(student);
    }
    
    public void saveStudent(Student student) {
        studentRepository.save(student);
    }
    
    public void deleteStudent(Long studentId) {
        // Get student to check section before deletion
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String section = student.getSection();
            
            studentRepository.deleteById(studentId);
            
            // Decrease section enrollment count
            if (section != null) {
                updateSectionEnrollment(section, -1);
            }
        } else {
            studentRepository.deleteById(studentId);
        }
    }
    
    public Optional<Student> getStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }
    
    // Faculty management
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }
    
    public void addFaculty(Faculty faculty) {
        try {
            // Validate required fields for Faculty Portal access
            if (faculty.getFacultyId() == null || faculty.getFacultyId().trim().isEmpty()) {
                throw new RuntimeException("Faculty ID is required for portal access");
            }
            if (faculty.getFirstName() == null || faculty.getFirstName().trim().isEmpty()) {
                throw new RuntimeException("First name is required");
            }
            if (faculty.getLastName() == null || faculty.getLastName().trim().isEmpty()) {
                throw new RuntimeException("Last name is required");
            }
            if (faculty.getEmail() == null || faculty.getEmail().trim().isEmpty()) {
                throw new RuntimeException("Email is required for portal access");
            }
            if (faculty.getDepartment() == null || faculty.getDepartment().trim().isEmpty()) {
                throw new RuntimeException("Department is required");
            }
            
            // Check if faculty ID already exists
            Optional<Faculty> existingFaculty = facultyRepository.findByFacultyId(faculty.getFacultyId());
            if (existingFaculty.isPresent()) {
                throw new RuntimeException("Faculty ID '" + faculty.getFacultyId() + "' already exists");
            }
            
            // Set up automatic Faculty Portal access
            setupFacultyPortalAccess(faculty);
            
            // Save the faculty with portal access
            facultyRepository.save(faculty);
            
        } catch (Exception e) {
            System.err.println("❌ Error creating faculty portal access: " + e.getMessage());
            throw new RuntimeException("Failed to create faculty portal access: " + e.getMessage(), e);
        }
    }
    
    /**
     * Sets up automatic Faculty Portal access for newly created faculty
     */
    private void setupFacultyPortalAccess(Faculty faculty) {
        // Ensure password is set - generate secure default if not provided
        if (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty()) {
            // Generate secure temporary password using faculty info
            String tempPassword = generateSecureTemporaryPassword(faculty);
            faculty.setPassword(tempPassword);
        }
        
        // Ensure status is set for portal access
        if (faculty.getStatus() == null || faculty.getStatus().trim().isEmpty()) {
            faculty.setStatus("ACTIVE");
        }
        
        // Ensure position is set
        if (faculty.getPosition() == null || faculty.getPosition().trim().isEmpty()) {
            faculty.setPosition("Instructor");
        }
        
        // Validate email format for portal notifications
        if (!isValidEmail(faculty.getEmail())) {
            throw new RuntimeException("Invalid email format: " + faculty.getEmail());
        }
    }
    
    /**
     * Generates a secure temporary password for faculty portal access
     */
    private String generateSecureTemporaryPassword(Faculty faculty) {
        // Create a secure but memorable temporary password
        // Format: FirstName + Last4DigitsOfFacultyId + "2024"
        String firstName = faculty.getFirstName().substring(0, 1).toUpperCase() + 
                          faculty.getFirstName().substring(1).toLowerCase();
        String facultyIdSuffix = faculty.getFacultyId().length() >= 4 ? 
                                faculty.getFacultyId().substring(faculty.getFacultyId().length() - 4) :
                                faculty.getFacultyId();
        return firstName + facultyIdSuffix + "2024";
    }
    
    /**
     * Validates email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    public void updateFaculty(Faculty faculty) {
        facultyRepository.save(faculty);
    }
    
    public void saveFaculty(Faculty faculty) {
        facultyRepository.save(faculty);
    }
    
    public void deleteFaculty(Long facultyId) {
        facultyRepository.deleteById(facultyId);
    }
    
    public Optional<Faculty> getFacultyById(Long facultyId) {
        return facultyRepository.findById(facultyId);
    }
    
    // Subject management
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
    
    public void addSubject(Subject subject) {
        subjectRepository.save(subject);
    }
    
    public void updateSubject(Subject subject) {
        subjectRepository.save(subject);
    }
    
    public void saveSubject(Subject subject) {
        subjectRepository.save(subject);
    }
    
    public void deleteSubject(Long subjectId) {
        try {
            // Check if subject exists
            Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
            if (!subjectOpt.isPresent()) {
                throw new RuntimeException("Subject not found with ID: " + subjectId);
            }
            
            // IMPORTANT: Remove subject from all curriculum packages first
            List<Curriculum> curriculumsWithSubject = curriculumRepository.findCurriculumsBySubjectId(subjectId);
            
            if (!curriculumsWithSubject.isEmpty()) {
                
                for (Curriculum curriculum : curriculumsWithSubject) {
                    // Remove the subject from the curriculum
                    curriculum.getSubjects().removeIf(s -> s.getId().equals(subjectId));
                    
                    // Recalculate total units
                    int totalUnits = curriculum.getSubjects().stream().mapToInt(Subject::getUnits).sum();
                    curriculum.setTotalUnits(totalUnits);
                    
                    // Save the updated curriculum
                    curriculumRepository.save(curriculum);
                }
            }
            
            // Now perform the deletion (cascading will handle schedules, enrollments, grades)
            subjectRepository.deleteById(subjectId);
            
        } catch (Exception e) {
            System.err.println("❌ Error deleting subject with ID " + subjectId + ": " + e.getMessage());
            
            // Provide more specific error messages
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException("Cannot delete subject: It is still referenced by other records. Please remove all references first.", e);
            } else {
                throw new RuntimeException("Failed to delete subject: " + e.getMessage(), e);
            }
        }
    }
    
    public Optional<Subject> getSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId);
    }
    
    // Schedule management
    public List<SubjectSchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
    
    public void addSchedule(String sectionCode, Long subjectId, Long facultyId, String dayOfWeek,
                           java.time.LocalTime startTime, java.time.LocalTime endTime, String room, Integer maxSlots,
                           String semester, String academicYear) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + facultyId));
        
        SubjectSchedule schedule = new SubjectSchedule(subject, faculty, sectionCode, dayOfWeek,
                                                     startTime, endTime, room, maxSlots, semester, academicYear);
        scheduleRepository.save(schedule);
    }
    
    // Backward compatibility method
    public void addSchedule(String sectionCode, Long subjectId, Long facultyId, String dayOfWeek,
                           java.time.LocalTime startTime, java.time.LocalTime endTime, String room, Integer maxSlots) {
        // Use current semester and academic year as defaults
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        String currentSemester = (month >= 8 || month <= 1) ? "1st" : "2nd";
        int year = now.getYear();
        String currentAcademicYear = (now.getMonthValue() >= 8) ? year + "-" + (year + 1) : (year - 1) + "-" + year;
        
        addSchedule(sectionCode, subjectId, facultyId, dayOfWeek, startTime, endTime, room, maxSlots, currentSemester, currentAcademicYear);
    }
    
    public void addSubjectWithMultipleSessions(String sectionCode, Long subjectId, Long facultyId, Integer maxSlots,
                                             String[] daysOfWeek, java.time.LocalTime[] startTimes, 
                                             java.time.LocalTime[] endTimes, String[] rooms,
                                             String semester, String academicYear) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + facultyId));

        for (int i = 0; i < daysOfWeek.length; i++) {
            SubjectSchedule schedule = new SubjectSchedule(subject, faculty, sectionCode,
                                                         daysOfWeek[i], startTimes[i], endTimes[i], 
                                                         rooms[i], maxSlots, semester, academicYear);
            scheduleRepository.save(schedule);
        }
    }
    
    // Backward compatibility method
    public void addSubjectWithMultipleSessions(String sectionCode, Long subjectId, Long facultyId, Integer maxSlots,
                                             String[] daysOfWeek, java.time.LocalTime[] startTimes, 
                                             java.time.LocalTime[] endTimes, String[] rooms) {
        // Use current semester and academic year as defaults
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        String currentSemester = (month >= 8 || month <= 1) ? "1st" : "2nd";
        int year = now.getYear();
        String currentAcademicYear = (now.getMonthValue() >= 8) ? year + "-" + (year + 1) : (year - 1) + "-" + year;
        
        addSubjectWithMultipleSessions(sectionCode, subjectId, facultyId, maxSlots, daysOfWeek, startTimes, endTimes, rooms, currentSemester, currentAcademicYear);
    }
    
    public void updateSchedule(SubjectSchedule schedule) {
        scheduleRepository.save(schedule);
    }
    
    public void saveSchedule(SubjectSchedule schedule) {
        scheduleRepository.save(schedule);
    }
    
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
    
    public Optional<SubjectSchedule> getScheduleById(Long scheduleId) {
        try {
            Optional<SubjectSchedule> scheduleOpt = scheduleRepository.findById(scheduleId);
            return scheduleOpt;
        } catch (Exception e) {
            System.err.println("❌ Error getting schedule by ID " + scheduleId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<SubjectSchedule> getSchedulesBySectionCode(String sectionCode) {
        return scheduleRepository.findBySectionCode(sectionCode);
    }
    
    // Enrollment management
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }
    
    public void saveEnrollment(Enrollment enrollment) {
        try {
            // Ensure entities are properly attached to the session
            if (enrollment.getStudent() != null) {
                Student attachedStudent = studentRepository.findById(enrollment.getStudent().getId()).orElse(null);
                if (attachedStudent == null) {
                    throw new RuntimeException("Student with ID " + enrollment.getStudent().getId() + " not found in database");
                }
                enrollment.setStudent(attachedStudent);
            }
            
            if (enrollment.getSubject() != null) {
                Subject attachedSubject = subjectRepository.findById(enrollment.getSubject().getId()).orElse(null);
                if (attachedSubject == null) {
                    throw new RuntimeException("Subject with ID " + enrollment.getSubject().getId() + " not found in database");
                }
                enrollment.setSubject(attachedSubject);
            }
            
            if (enrollment.getSubjectSchedule() != null) {
                SubjectSchedule attachedSchedule = scheduleRepository.findById(enrollment.getSubjectSchedule().getId()).orElse(null);
                if (attachedSchedule == null) {
                    throw new RuntimeException("Schedule with ID " + enrollment.getSubjectSchedule().getId() + " not found in database");
                }
                enrollment.setSubjectSchedule(attachedSchedule);
            }
            
            enrollmentRepository.save(enrollment);
            
        } catch (Exception e) {
            System.err.println("❌ Error in AdminService.saveEnrollment: " + e.getMessage());
            throw e; // Re-throw to preserve the original exception
        }
    }
    
    public void addEnrollment(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }
    
    public void updateEnrollment(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }
    
    public void deleteEnrollment(Long enrollmentId) {
        enrollmentRepository.deleteById(enrollmentId);
    }
    
    public Optional<Enrollment> getEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }
    
        // Curriculum management
    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    public void addCurriculum(Curriculum curriculum) {
        curriculumRepository.save(curriculum);
    }

    public void addCurriculumWithSubjects(Curriculum curriculum, List<Long> subjectIds) {
        // Save the curriculum first
        curriculumRepository.save(curriculum);
        
        // If subject IDs are provided, add them to the curriculum
        if (subjectIds != null && !subjectIds.isEmpty()) {
            List<Subject> subjects = subjectRepository.findAllById(subjectIds);
            curriculum.setSubjects(subjects);
            
            // Calculate total units
            int totalUnits = subjects.stream().mapToInt(Subject::getUnits).sum();
            curriculum.setTotalUnits(totalUnits);
            
            curriculumRepository.save(curriculum);
        }
    }

    public void updateCurriculum(Curriculum curriculum) {
        curriculumRepository.save(curriculum);
    }

    public void updateCurriculumWithSubjects(Curriculum curriculum, List<Long> subjectIds) {
        // Clear existing subjects first
        curriculum.setSubjects(new ArrayList<>());
        
        // If subject IDs are provided, add them to the curriculum
        if (subjectIds != null && !subjectIds.isEmpty()) {
            List<Subject> subjects = subjectRepository.findAllById(subjectIds);
            curriculum.setSubjects(subjects);
            
            // Calculate total units
            int totalUnits = subjects.stream().mapToInt(Subject::getUnits).sum();
            curriculum.setTotalUnits(totalUnits);
        } else {
            curriculum.setTotalUnits(0);
        }
        
        curriculumRepository.save(curriculum);
    }

    public void saveCurriculum(Curriculum curriculum) {
        curriculumRepository.save(curriculum);
    }

    public void deleteCurriculum(Long curriculumId) {
        curriculumRepository.deleteById(curriculumId);
    }

    public Optional<Curriculum> getCurriculumById(Long curriculumId) {
        return curriculumRepository.findById(curriculumId);
    }
    
    // Section management
    public List<Section> getAllSections() {
        List<Section> sections = sectionRepository.findAll();
        // Sort by course, then by section code
        sections.sort((a, b) -> {
            int courseComparison = a.getCourse().compareTo(b.getCourse());
            if (courseComparison != 0) {
                return courseComparison;
            }
            return a.getSectionCode().compareTo(b.getSectionCode());
        });
        return sections;
    }
    
    public List<Section> getAllSectionsSortedByCourse() {
        return getAllSections(); // Already sorted by course
    }
    
    public void addSection(Section section) {
        sectionRepository.save(section);
    }
    
    public void updateSection(Section section) {
        sectionRepository.save(section);
    }
    
    public void saveSection(Section section) {
        sectionRepository.save(section);
    }
    
    public void deleteSection(Long sectionId) {
        sectionRepository.deleteById(sectionId);
    }
    
    public Optional<Section> getSectionById(Long sectionId) {
        return sectionRepository.findById(sectionId);
    }
    
    // Method to get available sections (not at full capacity)
    public List<Section> getAvailableSections() {
        return sectionRepository.findAvailableSections();
    }
    
    // Method to get section by section code
    public Optional<Section> getSectionBySectionCode(String sectionCode) {
        return sectionRepository.findBySectionCode(sectionCode);
    }
    
    // Method to update section enrollment count
    public void updateSectionEnrollment(String sectionCode, int change) {
        Optional<Section> sectionOpt = sectionRepository.findBySectionCode(sectionCode);
        if (sectionOpt.isPresent()) {
            Section section = sectionOpt.get();
            section.setCurrentEnrollment(section.getCurrentEnrollment() + change);
            sectionRepository.save(section);
        }
    }
    
    // Dashboard statistics
    public long getTotalStudents() {
        return studentRepository.count();
    }
    
    public long getTotalFaculty() {
        return facultyRepository.count();
    }
    
    public long getTotalSubjects() {
        return subjectRepository.count();
    }
    
    public long getTotalEnrollments() {
        return enrollmentRepository.count();
    }
    
    public long getTotalSchedules() {
        return scheduleRepository.count();
    }
    
    public long getTotalCurriculums() {
        return curriculumRepository.count();
    }
    
    /**
     * Gets faculty portal access information for admin reference
     */
    public String getFacultyPortalAccessInfo(Long facultyId) {
        Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
        if (facultyOpt.isPresent()) {
            Faculty faculty = facultyOpt.get();
            return String.format(
                "Faculty Portal Access Info:\n" +
                "Faculty ID: %s\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Department: %s\n" +
                "Status: %s\n" +
                "Portal URL: /faculty/login\n" +
                "Login Instructions: Use Faculty ID and password to access portal",
                faculty.getFacultyId(),
                faculty.getFullName(),
                faculty.getEmail(),
                faculty.getDepartment(),
                faculty.getStatus()
            );
        }
        return "Faculty not found";
    }
    
    /**
     * Tests if faculty can access their portal (for admin verification)
     */
    public boolean testFacultyPortalAccess(String facultyId, String password) {
        try {
            Optional<Faculty> facultyOpt = facultyRepository.findByFacultyId(facultyId);
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                return password.equals(faculty.getPassword()) && "ACTIVE".equals(faculty.getStatus());
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error testing faculty portal access: " + e.getMessage());
            return false;
        }
    }
    
    // Curriculum Assignment Management
    
    /**
     * Assign a curriculum package to a student - makes subjects available for selection
     */
    public void assignCurriculumToStudent(Long studentId, Long curriculumId) {
        try {
            // Get current semester and academic year
            java.time.LocalDate now = java.time.LocalDate.now();
            int month = now.getMonthValue();
            String currentSemester = (month >= 8 || month <= 1) ? "1st" : "2nd";
            int year = now.getYear();
            String currentAcademicYear = (now.getMonthValue() >= 8) ? year + "-" + (year + 1) : (year - 1) + "-" + year;
            
            // Get entities
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
            Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Curriculum not found with ID: " + curriculumId));
            
            // Create and save assignment
            CurriculumAssignment assignment = new CurriculumAssignment(
                student, curriculum, "ADMIN_SYSTEM", currentSemester, currentAcademicYear);
            
            curriculumAssignmentRepository.save(assignment);
            
        } catch (Exception e) {
            System.err.println("❌ Error assigning curriculum to student: " + e.getMessage());
            throw new RuntimeException("Failed to assign curriculum to student: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if a student is already assigned to a curriculum package
     */
    public boolean isStudentAssignedToCurriculum(Long studentId, Long curriculumId) {
        return curriculumAssignmentRepository.findActiveAssignment(studentId, curriculumId).isPresent();
    }
    
    /**
     * Get all curriculum assignments for a student
     */
    public List<CurriculumAssignment> getStudentCurriculumAssignments(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        return curriculumAssignmentRepository.findActiveByStudent(student);
    }
    
    /**
     * Get all students assigned to a curriculum package
     */
    public List<CurriculumAssignment> getCurriculumAssignments(Long curriculumId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
            .orElseThrow(() -> new RuntimeException("Curriculum not found with ID: " + curriculumId));
        return curriculumAssignmentRepository.findActiveByCurriculum(curriculum);
    }
    
    /**
     * Remove a curriculum assignment
     */
    public void removeCurriculumAssignment(Long studentId, Long curriculumId) {
        Optional<CurriculumAssignment> assignmentOpt = curriculumAssignmentRepository.findActiveAssignment(studentId, curriculumId);
        if (assignmentOpt.isPresent()) {
            CurriculumAssignment assignment = assignmentOpt.get();
            assignment.setStatus("INACTIVE");
            curriculumAssignmentRepository.save(assignment);
        }
    }
    
    /**
     * Get curriculum IDs assigned to a student
     */
    public List<Long> getAssignedCurriculumIds(Student student) {
        return curriculumAssignmentRepository.findCurriculumIdsByStudent(student);
    }
    
    /**
     * Count students assigned to a curriculum
     */
    public Long countStudentsAssignedToCurriculum(Long curriculumId) {
        return curriculumAssignmentRepository.countActiveAssignmentsByCurriculum(curriculumId);
    }
    
    // Program management
    public List<Program> getAllPrograms() {
        return programRepository.findAllByOrderByProgramCodeAsc();
    }
    
    public List<Program> getActivePrograms() {
        return programRepository.findByStatusOrderByProgramCodeAsc("ACTIVE");
    }
    
    public void addProgram(Program program) {
        try {
            // Validate required fields
            if (program.getProgramCode() == null || program.getProgramCode().trim().isEmpty()) {
                throw new RuntimeException("Program code is required");
            }
            if (program.getProgramName() == null || program.getProgramName().trim().isEmpty()) {
                throw new RuntimeException("Program name is required");
            }
            if (program.getDepartment() == null || program.getDepartment().trim().isEmpty()) {
                throw new RuntimeException("Department is required");
            }
            
            // Convert program code to uppercase for consistency
            program.setProgramCode(program.getProgramCode().toUpperCase());
            
            // Check if program code already exists
            if (programRepository.existsByProgramCode(program.getProgramCode())) {
                throw new RuntimeException("Program code '" + program.getProgramCode() + "' already exists");
            }
            
            // Set default values if not provided
            if (program.getDegreeLevel() == null || program.getDegreeLevel().trim().isEmpty()) {
                program.setDegreeLevel("Bachelor");
            }
            if (program.getTotalYears() == null) {
                program.setTotalYears(4);
            }
            if (program.getTotalUnits() == null) {
                program.setTotalUnits(120);
            }
            if (program.getStatus() == null || program.getStatus().trim().isEmpty()) {
                program.setStatus("ACTIVE");
            }
            if (program.getEstablishedDate() == null) {
                program.setEstablishedDate(java.time.LocalDate.now());
            }
            
            programRepository.save(program);
            
        } catch (Exception e) {
            System.err.println("Error adding program: " + e.getMessage());
            throw new RuntimeException("Failed to add program: " + e.getMessage(), e);
        }
    }
    
    public void updateProgram(Program program) {
        try {
            // Validate required fields
            if (program.getProgramCode() == null || program.getProgramCode().trim().isEmpty()) {
                throw new RuntimeException("Program code is required");
            }
            if (program.getProgramName() == null || program.getProgramName().trim().isEmpty()) {
                throw new RuntimeException("Program name is required");
            }
            if (program.getDepartment() == null || program.getDepartment().trim().isEmpty()) {
                throw new RuntimeException("Department is required");
            }
            
            // Convert program code to uppercase for consistency
            program.setProgramCode(program.getProgramCode().toUpperCase());
            
            // Check if program code already exists for a different program
            Optional<Program> existingProgram = programRepository.findByProgramCode(program.getProgramCode());
            if (existingProgram.isPresent() && !existingProgram.get().getId().equals(program.getId())) {
                throw new RuntimeException("Program code '" + program.getProgramCode() + "' already exists");
            }
            
            programRepository.save(program);
            
        } catch (Exception e) {
            System.err.println("Error updating program: " + e.getMessage());
            throw new RuntimeException("Failed to update program: " + e.getMessage(), e);
        }
    }
    
    public void saveProgram(Program program) {
        programRepository.save(program);
    }
    
    public void deleteProgram(Long programId) {
        try {
            // Check if program exists
            Optional<Program> programOpt = programRepository.findById(programId);
            if (!programOpt.isPresent()) {
                throw new RuntimeException("Program not found with ID: " + programId);
            }
            
            Program program = programOpt.get();
            
            // Check if program has active students (via course field in Student entity)
            List<Student> studentsInProgram = studentRepository.findByCourse(program.getProgramCode());
            if (!studentsInProgram.isEmpty()) {
                throw new RuntimeException("Cannot delete program: " + studentsInProgram.size() + " students are enrolled in this program. Please transfer or graduate all students first.");
            }
            
            // Check if program has active sections (via course field in Section entity)
            List<Section> sectionsInProgram = sectionRepository.findByCourse(program.getProgramCode());
            long activeSectionCount = sectionsInProgram.stream()
                .filter(section -> "ACTIVE".equals(section.getStatus()))
                .count();
            
            if (activeSectionCount > 0) {
                throw new RuntimeException("Cannot delete program: " + activeSectionCount + " active sections exist for this program. Please remove or deactivate all sections first.");
            }
            
            // Check if program has curriculums (via course field in Curriculum entity)
            List<Curriculum> curriculumsInProgram = curriculumRepository.findByCourse(program.getProgramCode());
            long activeCurriculumCount = curriculumsInProgram.stream()
                .filter(curriculum -> "ACTIVE".equals(curriculum.getStatus()))
                .count();
            
            if (activeCurriculumCount > 0) {
                throw new RuntimeException("Cannot delete program: " + activeCurriculumCount + " active curriculums exist for this program. Please remove or deactivate all curriculums first.");
            }
            
            // If no dependencies, proceed with deletion
            programRepository.deleteById(programId);
            
        } catch (Exception e) {
            System.err.println("❌ Error deleting program with ID " + programId + ": " + e.getMessage());
            
            // Provide more specific error messages
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException("Cannot delete program: It is still referenced by other records. Please remove all references first.", e);
            } else {
                throw new RuntimeException("Failed to delete program: " + e.getMessage(), e);
            }
        }
    }
    
    public Optional<Program> getProgramById(Long programId) {
        return programRepository.findById(programId);
    }
    
    public Optional<Program> getProgramByCode(String programCode) {
        return programRepository.findByProgramCode(programCode != null ? programCode.toUpperCase() : null);
    }
    
    public List<Program> getProgramsByDepartment(String department) {
        return programRepository.findByDepartment(department);
    }
    
    public List<Program> getProgramsByStatus(String status) {
        return programRepository.findByStatus(status);
    }
    
    public long getTotalPrograms() {
        return programRepository.count();
    }
    
    public long getActiveProgramsCount() {
        return programRepository.findByStatus("ACTIVE").size();
    }
    
    // Enrollment Period Management
    public void createDefaultEnrollmentPeriod() {
        try {
            // Check if there's already an active enrollment period
            Optional<EnrollmentPeriod> existingPeriod = enrollmentPeriodRepository.findByIsActiveTrue();
            
            if (existingPeriod.isPresent()) {
                // Deactivate existing period first
                EnrollmentPeriod existing = existingPeriod.get();
                existing.setIsActive(false);
                enrollmentPeriodRepository.save(existing);
                System.out.println("Deactivated existing enrollment period: " + existing.getId());
            }
            
            // Get current semester and academic year
            java.time.LocalDate now = java.time.LocalDate.now();
            int month = now.getMonthValue();
            String currentSemester = (month >= 8 || month <= 1) ? "1st" : "2nd";
            int year = now.getYear();
            String currentAcademicYear = (now.getMonthValue() >= 8) ? year + "-" + (year + 1) : (year - 1) + "-" + year;
            
            // Create enrollment period that starts now and ends in 60 days (longer period)
            java.time.LocalDateTime startDate = java.time.LocalDateTime.now().minusMinutes(5); // Start 5 minutes ago to ensure it's active
            java.time.LocalDateTime endDate = startDate.plusDays(60); // 60 days instead of 30
            
            EnrollmentPeriod enrollmentPeriod = new EnrollmentPeriod(
                currentSemester,
                currentAcademicYear,
                startDate,
                endDate,
                true
            );
            
            enrollmentPeriodRepository.save(enrollmentPeriod);
            
            System.out.println("✅ Created new enrollment period:");
            System.out.println("   Semester: " + currentSemester);
            System.out.println("   Academic Year: " + currentAcademicYear);
            System.out.println("   Start Date: " + startDate);
            System.out.println("   End Date: " + endDate);
            System.out.println("   Is Active: " + true);
            System.out.println("   Current Time: " + java.time.LocalDateTime.now());
            
        } catch (Exception e) {
            System.err.println("Error creating default enrollment period: " + e.getMessage());
            throw new RuntimeException("Failed to create default enrollment period: " + e.getMessage(), e);
        }
    }
    
    public List<EnrollmentPeriod> getAllEnrollmentPeriods() {
        return enrollmentPeriodRepository.findAll();
    }
    
    public Optional<EnrollmentPeriod> getActiveEnrollmentPeriod() {
        return enrollmentPeriodRepository.findActiveEnrollmentPeriod();
    }
    
    public void updateEnrollmentPeriod(EnrollmentPeriod period) {
        enrollmentPeriodRepository.save(period);
    }
} 