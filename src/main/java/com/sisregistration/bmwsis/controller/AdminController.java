package com.sisregistration.bmwsis.controller;

import com.sisregistration.bmwsis.entity.Admin;
import com.sisregistration.bmwsis.entity.Student;
import com.sisregistration.bmwsis.entity.Faculty;
import com.sisregistration.bmwsis.entity.Subject;
import com.sisregistration.bmwsis.entity.SubjectSchedule;
import com.sisregistration.bmwsis.entity.Section;
import com.sisregistration.bmwsis.entity.Enrollment;
import com.sisregistration.bmwsis.entity.EnrollmentPeriod;
import com.sisregistration.bmwsis.entity.Curriculum;
import com.sisregistration.bmwsis.entity.CurriculumAssignment;
import com.sisregistration.bmwsis.entity.Program;
import com.sisregistration.bmwsis.entity.SystemSettings;
import com.sisregistration.bmwsis.service.AdminService;
import com.sisregistration.bmwsis.repository.EnrollmentPeriodRepository;
import com.sisregistration.bmwsis.repository.CurriculumAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private EnrollmentPeriodRepository enrollmentPeriodRepository;
    
    @Autowired
    private CurriculumAssignmentRepository curriculumAssignmentRepository;
    

    
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin-login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String adminId, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        Optional<Admin> adminOpt = adminService.authenticateAdmin(adminId, password);
        
        if (adminOpt.isPresent()) {
            session.setAttribute("loggedInAdmin", adminOpt.get());
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid admin ID or password");
            return "redirect:/admin/login";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            model.addAttribute("error", "Please log in to access the dashboard");
            return "redirect:/admin/login";
        }
        
        try {
            // Get counts for dashboard statistics
            long studentCount = adminService.getTotalStudents();
            long facultyCount = adminService.getTotalFaculty();
            long subjectCount = adminService.getTotalSubjects();
            long scheduleCount = adminService.getTotalSchedules();
            long sectionCount = adminService.getAllSections().size(); // Keep this as list for sections dropdown
            long enrollmentCount = adminService.getTotalEnrollments();
            long curriculumCount = adminService.getTotalCurriculums();
            long programCount = adminService.getTotalPrograms();
            
            // Get sections list for other dashboard components
            List<Section> sections = adminService.getAllSections();
            
            model.addAttribute("admin", admin);
            model.addAttribute("studentCount", studentCount);
            model.addAttribute("facultyCount", facultyCount);
            model.addAttribute("subjectCount", subjectCount);
            model.addAttribute("scheduleCount", scheduleCount);
            model.addAttribute("sectionCount", sectionCount);
            model.addAttribute("enrollmentCount", enrollmentCount);
            model.addAttribute("curriculumCount", curriculumCount);
            model.addAttribute("programCount", programCount);
            model.addAttribute("sections", sections);
            
        } catch (Exception e) {
            System.err.println("Error getting dashboard statistics: " + e.getMessage());
            
            // Set default values if there's an error
            model.addAttribute("admin", admin);
            model.addAttribute("studentCount", 0L);
            model.addAttribute("facultyCount", 0L);
            model.addAttribute("subjectCount", 0L);
            model.addAttribute("scheduleCount", 0L);
            model.addAttribute("sectionCount", 0L);
            model.addAttribute("enrollmentCount", 0L);
            model.addAttribute("curriculumCount", 0L);
            model.addAttribute("programCount", 0L);
            model.addAttribute("sections", new java.util.ArrayList<>());
        }
        
        return "admin-dashboard";
    }
    
    @GetMapping("/students")
    public String manageStudents(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        List<Student> students = adminService.getAllStudents();
        List<Section> sections = adminService.getAllSections();
        List<Program> programs = adminService.getAllPrograms();
        model.addAttribute("admin", admin);
        model.addAttribute("students", students);
        model.addAttribute("sections", sections);
        model.addAttribute("programs", programs);
        return "admin-students";
    }
    
    @GetMapping("/faculty")
    public String manageFaculty(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        List<Faculty> faculty = adminService.getAllFaculty();
        List<Program> programs = adminService.getAllPrograms();
        model.addAttribute("admin", admin);
        model.addAttribute("faculty", faculty);
        model.addAttribute("programs", programs);
        return "admin-faculty";
    }
    
    @GetMapping("/subjects")
    public String manageSubjects(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        List<Subject> subjects = adminService.getAllSubjects();
        List<Program> programs = adminService.getAllPrograms();
        
        // Group subjects by program
        java.util.Map<String, List<Subject>> subjectsByProgram = new java.util.LinkedHashMap<>();
        
        if (subjects != null) {
            for (Subject subject : subjects) {
                String program = subject.getCourse();
                if (program == null || program.trim().isEmpty()) {
                    program = "General";
                }
                
                if (!subjectsByProgram.containsKey(program)) {
                    subjectsByProgram.put(program, new java.util.ArrayList<>());
                }
                subjectsByProgram.get(program).add(subject);
            }
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("subjects", subjects);
        model.addAttribute("subjectsByProgram", subjectsByProgram);
        model.addAttribute("programs", programs);
        
        return "admin-subjects";
    }
    
    @GetMapping("/schedules")
    public String manageSchedules(HttpSession session, Model model, @RequestParam(required = false) String section) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        List<SubjectSchedule> schedules = adminService.getAllSchedules();
        List<Subject> subjects = adminService.getAllSubjects();
        List<Faculty> faculty = adminService.getAllFaculty();
        List<Section> sections = adminService.getAllSections();
        List<Program> programs = adminService.getAllPrograms();
        
        // Group schedules by faculty for better UI organization
        java.util.Map<Faculty, List<SubjectSchedule>> facultySchedules = new java.util.LinkedHashMap<>();
        for (SubjectSchedule schedule : schedules) {
            if (schedule.getFaculty() != null) {
                Faculty facultyMember = schedule.getFaculty();
                if (!facultySchedules.containsKey(facultyMember)) {
                    facultySchedules.put(facultyMember, new java.util.ArrayList<>());
                }
                facultySchedules.get(facultyMember).add(schedule);
            }
        }
        
        // Sort faculty by last name, then first name
        java.util.Map<Faculty, List<SubjectSchedule>> sortedFacultySchedules = new java.util.LinkedHashMap<>();
        java.util.List<Faculty> sortedFaculties = new java.util.ArrayList<>(facultySchedules.keySet());
        java.util.Collections.sort(sortedFaculties, new java.util.Comparator<Faculty>() {
            @Override
            public int compare(Faculty f1, Faculty f2) {
                int lastNameCompare = f1.getLastName().compareToIgnoreCase(f2.getLastName());
                if (lastNameCompare == 0) {
                    return f1.getFirstName().compareToIgnoreCase(f2.getFirstName());
                }
                return lastNameCompare;
            }
        });
        
        for (Faculty facultyMember : sortedFaculties) {
            sortedFacultySchedules.put(facultyMember, facultySchedules.get(facultyMember));
        }
        
        // Sort schedules within each faculty by day and time
        for (java.util.Map.Entry<Faculty, List<SubjectSchedule>> entry : sortedFacultySchedules.entrySet()) {
            List<SubjectSchedule> facultyScheduleList = entry.getValue();
            java.util.Collections.sort(facultyScheduleList, new java.util.Comparator<SubjectSchedule>() {
                @Override
                public int compare(SubjectSchedule s1, SubjectSchedule s2) {
                    // Define day order
                    java.util.Map<String, Integer> dayOrder = new java.util.HashMap<>();
                    dayOrder.put("Monday", 1);
                    dayOrder.put("Tuesday", 2);
                    dayOrder.put("Wednesday", 3);
                    dayOrder.put("Thursday", 4);
                    dayOrder.put("Friday", 5);
                    dayOrder.put("Saturday", 6);
                    dayOrder.put("Sunday", 7);
                    
                    int dayCompare = dayOrder.getOrDefault(s1.getDayOfWeek(), 8)
                        .compareTo(dayOrder.getOrDefault(s2.getDayOfWeek(), 8));
                    
                    if (dayCompare == 0) {
                        return s1.getStartTime().compareTo(s2.getStartTime());
                    }
                    return dayCompare;
                }
            });
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("schedules", schedules);
        model.addAttribute("facultySchedules", sortedFacultySchedules);
        model.addAttribute("subjects", subjects);
        model.addAttribute("faculty", faculty);
        model.addAttribute("sections", sections);
        model.addAttribute("programs", programs);
        model.addAttribute("selectedSection", section);
        return "admin-schedules";
    }
    
    @GetMapping("/curriculum")
    public String manageCurriculum(HttpSession session, Model model) {
            Admin admin = (Admin) session.getAttribute("loggedInAdmin");
            if (admin == null) {
                return "redirect:/admin/login";
            }
            
        try {
            List<Curriculum> curriculums = adminService.getAllCurriculums();
            List<Subject> subjects = adminService.getAllSubjects();
            List<Program> programs = adminService.getAllPrograms();
            
            model.addAttribute("admin", admin);
            model.addAttribute("curriculums", curriculums);
            model.addAttribute("subjects", subjects);
            model.addAttribute("programs", programs);
            
            return "admin-curriculum";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading curriculum data: " + e.getMessage());
            return "admin-curriculum";
        }
    }
    
    @GetMapping("/sections")
    public String manageSections(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
        List<Section> sections = adminService.getAllSections();
            List<Program> programs = adminService.getAllPrograms();
        
        model.addAttribute("admin", admin);
        model.addAttribute("sections", sections);
            model.addAttribute("programs", programs);
            
            return "admin-sections";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading section data: " + e.getMessage());
            model.addAttribute("sections", new java.util.ArrayList<>());
            model.addAttribute("programs", new java.util.ArrayList<>());
        return "admin-sections";
        }
    }
    
    @GetMapping("/enrollment")
    public String manageEnrollment(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        List<Enrollment> enrollments = adminService.getAllEnrollments();
        List<Student> students = adminService.getAllStudents();
        List<SubjectSchedule> schedules = adminService.getAllSchedules();
        List<Curriculum> curriculums = adminService.getAllCurriculums();
        List<Program> programs = adminService.getAllPrograms();
        
        // Group enrollments by student for better UI organization
        java.util.Map<Student, List<Enrollment>> studentEnrollments = new java.util.LinkedHashMap<>();
        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();
            if (!studentEnrollments.containsKey(student)) {
                studentEnrollments.put(student, new java.util.ArrayList<>());
            }
            studentEnrollments.get(student).add(enrollment);
        }
        
        // Sort students by last name, then first name
        java.util.Map<Student, List<Enrollment>> sortedStudentEnrollments = new java.util.LinkedHashMap<>();
        java.util.List<Student> sortedStudents = new java.util.ArrayList<>(studentEnrollments.keySet());
        java.util.Collections.sort(sortedStudents, new java.util.Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                int lastNameCompare = s1.getLastName().compareToIgnoreCase(s2.getLastName());
                if (lastNameCompare == 0) {
                    return s1.getFirstName().compareToIgnoreCase(s2.getFirstName());
                }
                return lastNameCompare;
            }
        });
        
        for (Student student : sortedStudents) {
            sortedStudentEnrollments.put(student, studentEnrollments.get(student));
        }
        
        // Sort enrollments within each student by subject code
        for (java.util.Map.Entry<Student, List<Enrollment>> entry : sortedStudentEnrollments.entrySet()) {
            List<Enrollment> studentEnrollmentList = entry.getValue();
            java.util.Collections.sort(studentEnrollmentList, new java.util.Comparator<Enrollment>() {
                @Override
                public int compare(Enrollment e1, Enrollment e2) {
                    String code1 = e1.getSubject() != null ? e1.getSubject().getSubjectCode() : "ZZZ";
                    String code2 = e2.getSubject() != null ? e2.getSubject().getSubjectCode() : "ZZZ";
                    return code1.compareToIgnoreCase(code2);
                }
            });
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("studentEnrollments", sortedStudentEnrollments);
        model.addAttribute("students", students);
        model.addAttribute("schedules", schedules);
        model.addAttribute("curriculums", curriculums);
        model.addAttribute("programs", programs);
        return "admin-enrollment";
    }
    
    @PostMapping("/enrollment/assign-curriculum")
    public String assignCurriculumToStudent(@RequestParam Long studentId,
                                           @RequestParam Long curriculumId,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // Get student and curriculum
            Optional<Student> studentOpt = adminService.getStudentById(studentId);
            Optional<Curriculum> curriculumOpt = adminService.getCurriculumById(curriculumId);
            
            if (!studentOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Student not found");
                return "redirect:/admin/enrollment";
            }
            
            if (!curriculumOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Curriculum package not found");
                return "redirect:/admin/enrollment";
            }
            
            Student student = studentOpt.get();
            Curriculum curriculum = curriculumOpt.get();
            
            // NEW APPROACH: Create curriculum assignment instead of auto-enrolling
            // This makes subjects available for student selection rather than auto-enrolling them
            
            // Check if student already has this curriculum package assigned
            boolean alreadyAssigned = adminService.isStudentAssignedToCurriculum(student.getId(), curriculum.getId());
            if (alreadyAssigned) {
                redirectAttributes.addFlashAttribute("warning", 
                    "Student " + student.getFullName() + " is already assigned to curriculum package: " + curriculum.getCurriculumName());
                return "redirect:/admin/enrollment";
            }
            
            // Create curriculum assignment record
            adminService.assignCurriculumToStudent(student.getId(), curriculum.getId());
            
            // Count available subjects in the package
            List<Subject> curriculumSubjects = curriculum.getSubjects();
            int availableSubjectCount = curriculumSubjects != null ? curriculumSubjects.size() : 0;
            
            // Build success message
            StringBuilder resultMessage = new StringBuilder();
            resultMessage.append("✅ Curriculum package assigned successfully! ");
            resultMessage.append(student.getFullName()).append(" can now choose from ")
                         .append(availableSubjectCount).append(" subjects in ")
                         .append(curriculum.getCurriculumName()).append(". ");
            resultMessage.append("The student will see these subjects in their enrollment portal.");
            
            redirectAttributes.addFlashAttribute("success", resultMessage.toString());
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error assigning curriculum: " + e.getMessage());
        }
        
        return "redirect:/admin/enrollment";
    }
    
    // Student Management

    // Faculty Management
    @PostMapping("/faculty/add")
    public String addFaculty(@ModelAttribute Faculty faculty,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Faculty added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }
    
    // Subject Management
    @PostMapping("/subject/add")
    public String addSubject(@ModelAttribute Subject subject,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addSubject(subject);
            redirectAttributes.addFlashAttribute("success", "Subject added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding subject: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }
    
    // AJAX endpoints for modern UI
    
    // Students AJAX
    @PostMapping("/students/add")
    public String addStudentNew(@ModelAttribute Student student,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please log in again.");
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student '" + student.getFirstName() + " " + student.getLastName() + "' added successfully!");
            
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
            
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "An unexpected error occurred while adding the student.";
            }
            
            redirectAttributes.addFlashAttribute("error", "Failed to add student: " + errorMessage);
        }
        
        return "redirect:/admin/students";
    }
    
    @GetMapping("/students/{id}")
    @ResponseBody
    public ResponseEntity<Student> getStudent(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Student> student = adminService.getStudentById(id);
        return student.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Long id,
                               @ModelAttribute Student student,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            student.setId(id);
            adminService.updateStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }
    
    @DeleteMapping("/students/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteStudentAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteStudent(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting student: " + e.getMessage());
        }
    }
    
    // Sections AJAX
    @GetMapping("/sections/available")
    @ResponseBody
    public ResponseEntity<List<Section>> getAvailableSections(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Section> sections = adminService.getAvailableSections();
        return ResponseEntity.ok(sections);
    }
    
    // Faculty AJAX
    @GetMapping("/faculty/{id}")
    @ResponseBody
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Faculty> faculty = adminService.getFacultyById(id);
        return faculty.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/faculty/update/{id}")
    public String updateFaculty(@PathVariable Long id,
                               @ModelAttribute Faculty faculty,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            faculty.setId(id);
            adminService.updateFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Faculty updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating faculty: " + e.getMessage());
        }
        return "redirect:/admin/faculty";
    }
    
    @DeleteMapping("/faculty/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteFacultyAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteFaculty(id);
            return ResponseEntity.ok("Faculty deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting faculty: " + e.getMessage());
        }
    }
    
    @GetMapping("/faculty/{id}/portal-access")
    @ResponseBody
    public ResponseEntity<String> getFacultyPortalAccess(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String accessInfo = adminService.getFacultyPortalAccessInfo(id);
            return ResponseEntity.ok(accessInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error getting faculty portal access info: " + e.getMessage());
        }
    }
    
    @GetMapping("/faculty/{id}/section-assignments")
    @ResponseBody
    public ResponseEntity<String> getFacultySectionAssignments(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Optional<Faculty> facultyOpt = adminService.getFacultyById(id);
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                
                // Get section assignments directly from AdminService
                // Note: FacultyService could be injected here for more complex queries
                
                List<SubjectSchedule> schedules = adminService.getAllSchedules();
                java.util.List<SubjectSchedule> facultySchedules = new java.util.ArrayList<>();
                for (SubjectSchedule schedule : schedules) {
                    if (schedule.getFaculty() != null && schedule.getFaculty().getId().equals(faculty.getId())) {
                        facultySchedules.add(schedule);
                    }
                }
                
                java.util.Set<String> sections = new java.util.HashSet<>();
                for (SubjectSchedule schedule : facultySchedules) {
                    sections.add(schedule.getSectionCode());
                }
                
                java.util.Set<String> subjects = new java.util.HashSet<>();
                for (SubjectSchedule schedule : facultySchedules) {
                    subjects.add(schedule.getSubject().getSubjectCode());
                }
                
                StringBuilder scheduleDetails = new StringBuilder();
                for (SubjectSchedule schedule : facultySchedules) {
                    scheduleDetails.append("• ").append(schedule.getSectionCode())
                                 .append(" - ").append(schedule.getSubject().getSubjectCode())
                                 .append(" (").append(schedule.getDayOfWeek())
                                 .append(" ").append(schedule.getTimeRange()).append(")\n");
                }
                
                String assignmentInfo = String.format(
                    "Faculty Section Assignments:\n\n" +
                    "Faculty: %s (%s)\n" +
                    "Department: %s\n\n" +
                    "Assigned Sections: %s\n" +
                    "Teaching Subjects: %s\n" +
                    "Total Schedules: %d\n\n" +
                    "Schedule Details:\n%s",
                    faculty.getFullName(),
                    faculty.getFacultyId(),
                    faculty.getDepartment(),
                    sections.isEmpty() ? "None" : String.join(", ", sections),
                    subjects.isEmpty() ? "None" : String.join(", ", subjects),
                    facultySchedules.size(),
                    scheduleDetails.toString()
                );
                
                return ResponseEntity.ok(assignmentInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error getting faculty section assignments: " + e.getMessage());
        }
    }
    
    @PostMapping("/faculty/{id}/test-access")
    @ResponseBody
    public ResponseEntity<String> testFacultyPortalAccess(@PathVariable Long id, 
                                                         @RequestParam String password,
                                                         HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Optional<Faculty> facultyOpt = adminService.getFacultyById(id);
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                boolean canAccess = adminService.testFacultyPortalAccess(faculty.getFacultyId(), password);
                
                if (canAccess) {
                    return ResponseEntity.ok("✅ Faculty can successfully access their portal!\n" +
                                           "Portal URL: /faculty/login\n" +
                                           "Faculty ID: " + faculty.getFacultyId());
                } else {
                    return ResponseEntity.ok("❌ Faculty cannot access portal. Please check credentials and status.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error testing faculty portal access: " + e.getMessage());
        }
    }
    
    // Subjects AJAX
    @PostMapping("/subjects/add")
    public String addSubjectNew(@ModelAttribute Subject subject,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addSubject(subject);
            redirectAttributes.addFlashAttribute("success", "Subject added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding subject: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }
    
    @GetMapping("/subjects/{id}")
    @ResponseBody
    public ResponseEntity<Subject> getSubject(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Subject> subject = adminService.getSubjectById(id);
        return subject.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/subjects/update/{id}")
    public String updateSubject(@PathVariable Long id,
                               @ModelAttribute Subject subject,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            subject.setId(id);
            adminService.updateSubject(subject);
            redirectAttributes.addFlashAttribute("success", "Subject updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating subject: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }
    
    @DeleteMapping("/subjects/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteSubjectAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not logged in");
        }
        
        try {
            adminService.deleteSubject(id);
            return ResponseEntity.ok("Subject deleted successfully");
        } catch (Exception e) {
            System.err.println("❌ Error in deleteSubjectAjax: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting subject: " + e.getMessage());
        }
    }
    
    // Schedules AJAX
    @PostMapping("/schedules/add")
    public String addSchedule(@RequestParam String sectionCode,
                             @RequestParam Long subjectId,
                             @RequestParam("faculty.id") Long facultyId,
                             @RequestParam String dayOfWeek,
                             @RequestParam LocalTime startTime,
                             @RequestParam LocalTime endTime,
                             @RequestParam String room,
                             @RequestParam Integer maxSlots,
                             @RequestParam String semester,
                             @RequestParam String academicYear,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addSchedule(sectionCode, subjectId, facultyId, dayOfWeek, startTime, endTime, room, maxSlots, semester, academicYear);
            redirectAttributes.addFlashAttribute("success", "Schedule added successfully for " + semester + " Semester " + academicYear);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding schedule: " + e.getMessage());
        }
        return "redirect:/admin/schedules";
    }
    
    @PostMapping("/schedules/add-subject")
    public String addSubjectWithSessions(@RequestParam String sectionCode,
                                       @RequestParam Long subjectId,
                                       @RequestParam Long facultyId,
                                       @RequestParam Integer maxSlots,
                                       @RequestParam(name = "dayOfWeek") String[] daysOfWeek,
                                       @RequestParam(name = "startTime") LocalTime[] startTimes,
                                       @RequestParam(name = "endTime") LocalTime[] endTimes,
                                       @RequestParam(name = "room") String[] rooms,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addSubjectWithMultipleSessions(sectionCode, subjectId, facultyId, maxSlots, daysOfWeek, startTimes, endTimes, rooms);
            redirectAttributes.addFlashAttribute("success", "Subject with schedules added successfully for section " + sectionCode);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding subject with sessions: " + e.getMessage());
        }
        return "redirect:/admin/schedules";
    }
    
    @GetMapping("/schedules/{id}")
    @ResponseBody
    public ResponseEntity<SubjectSchedule> getSchedule(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<SubjectSchedule> schedule = adminService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/schedules/update/{id}")
    public String updateSchedule(@PathVariable Long id,
                                @ModelAttribute SubjectSchedule schedule,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            schedule.setId(id);
            adminService.updateSchedule(schedule);
            redirectAttributes.addFlashAttribute("success", "Schedule updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating schedule: " + e.getMessage());
        }
        return "redirect:/admin/schedules";
    }
    
    @DeleteMapping("/schedules/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteScheduleAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteSchedule(id);
            return ResponseEntity.ok("Schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting schedule: " + e.getMessage());
        }
    }
    
    @GetMapping("/schedules/section/{sectionCode}")
    @ResponseBody
    public ResponseEntity<List<SubjectSchedule>> getSchedulesBySectionCode(@PathVariable String sectionCode, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            List<SubjectSchedule> schedules = adminService.getSchedulesBySectionCode(sectionCode);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Curriculum AJAX
    @PostMapping("/curriculum/add")
    public String addCurriculum(@ModelAttribute Curriculum curriculum,
                               @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addCurriculumWithSubjects(curriculum, subjectIds);
            redirectAttributes.addFlashAttribute("success", "Curriculum package created successfully with " + 
                (subjectIds != null ? subjectIds.size() : 0) + " subjects");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating curriculum: " + e.getMessage());
        }
        return "redirect:/admin/curriculum";
    }
    
    @GetMapping("/curriculum/{id}")
    @ResponseBody
    public ResponseEntity<Curriculum> getCurriculum(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Curriculum> curriculum = adminService.getCurriculumById(id);
        return curriculum.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/curriculum/update/{id}")
    public String updateCurriculum(@PathVariable Long id,
                                  @ModelAttribute Curriculum curriculum,
                                  @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            curriculum.setId(id);
            adminService.updateCurriculumWithSubjects(curriculum, subjectIds);
            redirectAttributes.addFlashAttribute("success", "Curriculum package updated successfully with " + 
                (subjectIds != null ? subjectIds.size() : 0) + " subjects");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating curriculum: " + e.getMessage());
        }
        return "redirect:/admin/curriculum";
    }
    
    @DeleteMapping("/curriculum/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteCurriculumAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteCurriculum(id);
            return ResponseEntity.ok("Curriculum deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting curriculum: " + e.getMessage());
        }
    }
    
    // Sections AJAX
    @PostMapping("/sections/add")
    public String addSection(@ModelAttribute Section section,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addSection(section);
            redirectAttributes.addFlashAttribute("success", "Section added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding section: " + e.getMessage());
        }
        return "redirect:/admin/sections";
    }
    
    @GetMapping("/sections/{id}")
    @ResponseBody
    public ResponseEntity<Section> getSection(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Section> section = adminService.getSectionById(id);
        return section.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/sections/update/{id}")
    public String updateSection(@PathVariable Long id,
                               @ModelAttribute Section section,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            section.setId(id);
            adminService.updateSection(section);
            redirectAttributes.addFlashAttribute("success", "Section updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating section: " + e.getMessage());
        }
        return "redirect:/admin/sections";
    }
    
    @DeleteMapping("/sections/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteSectionAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteSection(id);
            return ResponseEntity.ok("Section deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting section: " + e.getMessage());
        }
    }
    
    // Enrollment AJAX
    @PostMapping("/enrollment/add")
    public String addEnrollment(@RequestParam Long studentId,
                               @RequestParam Long scheduleId,
                               @RequestParam String status,
                               @RequestParam(required = false) String enrollmentDate,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // Get student and schedule entities
            Optional<Student> studentOpt = adminService.getStudentById(studentId);
            Optional<SubjectSchedule> scheduleOpt = adminService.getScheduleById(scheduleId);
            
            if (!studentOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Student not found with ID: " + studentId);
                return "redirect:/admin/enrollment";
            }
            
            if (!scheduleOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Schedule not found with ID: " + scheduleId);
                return "redirect:/admin/enrollment";
            }
            
            Student student = studentOpt.get();
            SubjectSchedule schedule = scheduleOpt.get();
            
            // Create enrollment
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setSubject(schedule.getSubject());
            enrollment.setSubjectSchedule(schedule);
            enrollment.setStatus(status);
            enrollment.setSemester(getCurrentSemester());
            enrollment.setAcademicYear(getCurrentAcademicYear());
            
            if (enrollmentDate != null && !enrollmentDate.trim().isEmpty()) {
                try {
                    enrollment.setEnrollmentDate(java.time.LocalDateTime.parse(enrollmentDate + "T00:00:00"));
                } catch (Exception e) {
                    enrollment.setEnrollmentDate(java.time.LocalDateTime.now());
                }
            } else {
                enrollment.setEnrollmentDate(java.time.LocalDateTime.now());
            }
            
            adminService.saveEnrollment(enrollment);
            
            redirectAttributes.addFlashAttribute("success", "Enrollment created successfully for " + 
                student.getFullName() + " in " + schedule.getSubject().getSubjectCode());
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating enrollment: " + e.getMessage());
        }
        
        return "redirect:/admin/enrollment";
    }
    
    @GetMapping("/enrollment/{id}")
    @ResponseBody
    public ResponseEntity<Enrollment> getEnrollment(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Enrollment> enrollment = adminService.getEnrollmentById(id);
        return enrollment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/enrollment/update/{id}")
    public String updateEnrollment(@PathVariable Long id,
                                  @RequestParam String status,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            Optional<Enrollment> enrollmentOpt = adminService.getEnrollmentById(id);
            if (enrollmentOpt.isPresent()) {
                Enrollment enrollment = enrollmentOpt.get();
                enrollment.setStatus(status);
                adminService.saveEnrollment(enrollment);
                redirectAttributes.addFlashAttribute("success", "Enrollment status updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Enrollment not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating enrollment: " + e.getMessage());
        }
        return "redirect:/admin/enrollment";
    }
    
    @DeleteMapping("/enrollment/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteEnrollmentAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteEnrollment(id);
            return ResponseEntity.ok("Enrollment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting enrollment: " + e.getMessage());
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully");
        return "redirect:/admin/login";
    }
    


    @GetMapping("/settings")
    public String showSettings(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            SystemSettings settings = adminService.getSystemSettings();
            model.addAttribute("admin", admin);
            model.addAttribute("settings", settings);
            return "admin-settings";
        } catch (Exception e) {
            // If there's an error getting settings, create a default one
            model.addAttribute("admin", admin);
            
            // Create a simple default settings object
            SystemSettings defaultSettings = new SystemSettings();
            defaultSettings.setSystemName("BMW Student Information System");
            defaultSettings.setInstitutionName("BMW College");
            defaultSettings.setAcademicYear("2024-2025");
            defaultSettings.setCurrentSemester("2nd");
            defaultSettings.setEnrollmentOpen(true);
            defaultSettings.setMaxUnitsPerStudent(24);
            defaultSettings.setPassingGrade(75);
            defaultSettings.setMidtermWeight(40);
            defaultSettings.setFinalWeight(60);
            defaultSettings.setGradeScale("A-F");
            defaultSettings.setEmailNotifications(true);
            defaultSettings.setSmsNotifications(false);
            defaultSettings.setEnrollmentNotifications(true);
            defaultSettings.setGradeNotifications(true);
            defaultSettings.setSessionTimeout(60);
            defaultSettings.setPasswordMinLength(8);
            defaultSettings.setRequirePasswordChange(true);
            defaultSettings.setTwoFactorAuth(false);
            
            model.addAttribute("settings", defaultSettings);
            model.addAttribute("error", "Unable to load settings from database. Using default values. Error: " + e.getMessage());
            return "admin-settings";
        }
    }
    
    @PostMapping("/settings/save")
    public String saveSettings(@RequestParam String systemName,
                              @RequestParam String institutionName,
                              @RequestParam String academicYear,
                              @RequestParam String currentSemester,
                              @RequestParam(defaultValue = "false") boolean enrollmentOpen,
                              @RequestParam String enrollmentStartDate,
                              @RequestParam String enrollmentEndDate,
                              @RequestParam int maxUnitsPerStudent,
                              @RequestParam int passingGrade,
                              @RequestParam int midtermWeight,
                              @RequestParam int finalWeight,
                              @RequestParam String gradeScale,
                              @RequestParam(defaultValue = "false") boolean emailNotifications,
                              @RequestParam(defaultValue = "false") boolean smsNotifications,
                              @RequestParam(defaultValue = "false") boolean enrollmentNotifications,
                              @RequestParam(defaultValue = "false") boolean gradeNotifications,
                              @RequestParam int sessionTimeout,
                              @RequestParam int passwordMinLength,
                              @RequestParam(defaultValue = "false") boolean requirePasswordChange,
                              @RequestParam(defaultValue = "false") boolean twoFactorAuth,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // Validate that midterm and final weights total 100%
            if (midtermWeight + finalWeight != 100) {
                redirectAttributes.addFlashAttribute("error", "Midterm and Final weights must total 100%");
                return "redirect:/admin/settings";
            }
            
            // Get existing settings or create new
            SystemSettings settings;
            try {
                settings = adminService.getSystemSettings();
            } catch (Exception e) {
                settings = new SystemSettings();
            }
            
            // Set all the values
            settings.setSystemName(systemName);
            settings.setInstitutionName(institutionName);
            settings.setAcademicYear(academicYear);
            settings.setCurrentSemester(currentSemester);
            settings.setEnrollmentOpen(enrollmentOpen);
            settings.setMaxUnitsPerStudent(maxUnitsPerStudent);
            settings.setPassingGrade(passingGrade);
            settings.setMidtermWeight(midtermWeight);
            settings.setFinalWeight(finalWeight);
            settings.setGradeScale(gradeScale);
            settings.setEmailNotifications(emailNotifications);
            settings.setSmsNotifications(smsNotifications);
            settings.setEnrollmentNotifications(enrollmentNotifications);
            settings.setGradeNotifications(gradeNotifications);
            settings.setSessionTimeout(sessionTimeout);
            settings.setPasswordMinLength(passwordMinLength);
            settings.setRequirePasswordChange(requirePasswordChange);
            settings.setTwoFactorAuth(twoFactorAuth);
            
            // Parse dates
            if (enrollmentStartDate != null && !enrollmentStartDate.isEmpty()) {
                settings.setEnrollmentStartDate(java.time.LocalDate.parse(enrollmentStartDate));
            }
            if (enrollmentEndDate != null && !enrollmentEndDate.isEmpty()) {
                settings.setEnrollmentEndDate(java.time.LocalDate.parse(enrollmentEndDate));
            }
            
            adminService.saveSystemSettings(settings);
            redirectAttributes.addFlashAttribute("success", "Settings saved successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving settings: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }
    
    // Simple Student Management - No Authentication Required
    @GetMapping("/simple-students")
    public String simpleStudentManagement(Model model) {
        try {
            List<Student> students = adminService.getAllStudents();
            List<Section> sections = adminService.getAllSections();
            List<Program> programs = adminService.getAllPrograms();
            
            model.addAttribute("students", students);
            model.addAttribute("sections", sections);
            model.addAttribute("programs", programs);
            model.addAttribute("newStudent", new Student());
            
            return "simple-student-management";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading data: " + e.getMessage());
            model.addAttribute("students", new java.util.ArrayList<>());
            model.addAttribute("sections", new java.util.ArrayList<>());
            model.addAttribute("programs", new java.util.ArrayList<>());
            model.addAttribute("newStudent", new Student());
            return "simple-student-management";
        }
    }
    
    @PostMapping("/simple-students/add")
    public String addSimpleStudent(@ModelAttribute Student student, 
                                  RedirectAttributes redirectAttributes) {
        try {
            // Set default password if not provided
            if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
                student.setPassword("student123");
            }
            
            adminService.addStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding student: " + e.getMessage());
        }
        return "redirect:/admin/simple-students";
    }
    
    @PostMapping("/simple-students/delete/{id}")
    public String deleteSimpleStudent(@PathVariable Long id, 
                                     RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/simple-students";
    }
    
    @GetMapping("/simple-students/edit/{id}")
    public String editSimpleStudent(@PathVariable Long id, Model model) {
        try {
            Optional<Student> studentOpt = adminService.getStudentById(id);
            if (studentOpt.isPresent()) {
                List<Section> sections = adminService.getAllSections();
                List<Program> programs = adminService.getAllPrograms();
                model.addAttribute("student", studentOpt.get());
                model.addAttribute("sections", sections);
                model.addAttribute("programs", programs);
                return "simple-edit-student";
            } else {
                model.addAttribute("error", "Student not found");
                return "redirect:/admin/simple-students";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading student: " + e.getMessage());
            return "redirect:/admin/simple-students";
        }
    }
    
    @PostMapping("/simple-students/update/{id}")
    public String updateSimpleStudent(@PathVariable Long id, 
                                     @ModelAttribute Student student,
                                     RedirectAttributes redirectAttributes) {
        try {
            student.setId(id);
            adminService.updateStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating student: " + e.getMessage());
        }
        return "redirect:/admin/simple-students";
    }
    
    private String getCurrentSemester() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int month = now.getMonthValue();
        return (month >= 8 || month <= 1) ? "1st" : "2nd";
    }
    
    private String getCurrentAcademicYear() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        return (now.getMonthValue() >= 8) ? year + "-" + (year + 1) : (year - 1) + "-" + year;
    }
    
    // Program Management
    @GetMapping("/programs")
    public String managePrograms(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            List<Program> programs = adminService.getAllPrograms();
            model.addAttribute("admin", admin);
            model.addAttribute("programs", programs);
            return "admin-programs";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading programs: " + e.getMessage());
            model.addAttribute("admin", admin);
            model.addAttribute("programs", new java.util.ArrayList<>());
            return "admin-programs";
        }
    }
    
    @PostMapping("/programs/add")
    public String addProgram(@ModelAttribute Program program,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.addProgram(program);
            redirectAttributes.addFlashAttribute("success", "Program '" + program.getProgramCode() + "' added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding program: " + e.getMessage());
        }
        return "redirect:/admin/programs";
    }
    
    @GetMapping("/programs/{id}")
    @ResponseBody
    public ResponseEntity<Program> getProgram(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Program> program = adminService.getProgramById(id);
        return program.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/programs/update/{id}")
    public String updateProgram(@PathVariable Long id,
                               @ModelAttribute Program program,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            program.setId(id);
            adminService.updateProgram(program);
            redirectAttributes.addFlashAttribute("success", "Program updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating program: " + e.getMessage());
        }
        return "redirect:/admin/programs";
    }
    
    @DeleteMapping("/programs/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProgramAjax(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            adminService.deleteProgram(id);
            return ResponseEntity.ok("Program deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting program: " + e.getMessage());
        }
    }
    
    @GetMapping("/programs/by-department/{department}")
    @ResponseBody
    public ResponseEntity<List<Program>> getProgramsByDepartment(@PathVariable String department, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Program> programs = adminService.getProgramsByDepartment(department);
        return ResponseEntity.ok(programs);
    }
    
    @GetMapping("/programs/active")
    @ResponseBody
    public ResponseEntity<List<Program>> getActivePrograms(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Program> activePrograms = adminService.getActivePrograms();
        return ResponseEntity.ok(activePrograms);
    }
    
    @PostMapping("/programs/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<String> toggleProgramStatus(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Optional<Program> programOpt = adminService.getProgramById(id);
            if (programOpt.isPresent()) {
                Program program = programOpt.get();
                String newStatus = "ACTIVE".equals(program.getStatus()) ? "INACTIVE" : "ACTIVE";
                program.setStatus(newStatus);
                adminService.updateProgram(program);
                return ResponseEntity.ok("Program status changed to " + newStatus);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error updating program status: " + e.getMessage());
        }
    }

    @PostMapping("/enrollment/create-default-period")
    public String createDefaultEnrollmentPeriod(HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            adminService.createDefaultEnrollmentPeriod();
            redirectAttributes.addFlashAttribute("success", "Default enrollment period created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating default enrollment period: " + e.getMessage());
        }
        return "redirect:/admin/enrollment";
    }

    @PostMapping("/students/assign-default-curriculum/{studentId}")
    @ResponseBody
    public ResponseEntity<String> assignDefaultCurriculum(@PathVariable Long studentId, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not logged in");
        }
        
        try {
            Optional<Student> studentOpt = adminService.getStudentById(studentId);
            if (!studentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }
            
            Student student = studentOpt.get();
            
            // Find a curriculum that matches the student's course and year level
            List<Curriculum> availableCurriculums = adminService.getAllCurriculums();
            Curriculum defaultCurriculum = null;
            
            for (Curriculum curriculum : availableCurriculums) {
                if (curriculum.getCourse().equalsIgnoreCase(student.getCourse()) && 
                    curriculum.getYearLevel() == student.getYearLevel()) {
                    defaultCurriculum = curriculum;
                    break;
                }
            }
            
            // If no exact match, find any active curriculum
            if (defaultCurriculum == null && !availableCurriculums.isEmpty()) {
                for (Curriculum curriculum : availableCurriculums) {
                    if ("ACTIVE".equalsIgnoreCase(curriculum.getStatus())) {
                        defaultCurriculum = curriculum;
                        break;
                    }
                }
            }
            
            // If still no curriculum, use the first available one
            if (defaultCurriculum == null && !availableCurriculums.isEmpty()) {
                defaultCurriculum = availableCurriculums.get(0);
            }
            
            if (defaultCurriculum == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No curriculum available to assign");
            }
            
            // Create curriculum assignment
            CurriculumAssignment assignment = new CurriculumAssignment();
            assignment.setStudent(student);
            assignment.setCurriculum(defaultCurriculum);
            assignment.setAssignedDate(java.time.LocalDateTime.now());
            assignment.setStatus("ACTIVE");
            assignment.setSemester(getCurrentSemester());
            assignment.setAcademicYear(getCurrentAcademicYear());
            
            curriculumAssignmentRepository.save(assignment);
            
            return ResponseEntity.ok("Successfully assigned curriculum: " + defaultCurriculum.getCurriculumName() + 
                                   " to student: " + student.getFirstName() + " " + student.getLastName());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error assigning curriculum: " + e.getMessage());
        }
    }

    @GetMapping("/enrollment/debug")
    @ResponseBody
    public ResponseEntity<String> debugEnrollmentStatus(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin not logged in");
        }
        
        try {
            StringBuilder debug = new StringBuilder();
            debug.append("=== ENROLLMENT PERIOD DEBUG INFO ===\n\n");
            
            // Check enrollment periods
            List<EnrollmentPeriod> allPeriods = enrollmentPeriodRepository.findAll();
            debug.append("Total enrollment periods in database: ").append(allPeriods.size()).append("\n\n");
            
            for (EnrollmentPeriod period : allPeriods) {
                debug.append("Period ID: ").append(period.getId()).append("\n");
                debug.append("  Semester: ").append(period.getSemester()).append("\n");
                debug.append("  Academic Year: ").append(period.getAcademicYear()).append("\n");
                debug.append("  Start Date: ").append(period.getStartDate()).append("\n");
                debug.append("  End Date: ").append(period.getEndDate()).append("\n");
                debug.append("  Is Active: ").append(period.getIsActive()).append("\n");
                debug.append("  Is Open: ").append(period.isEnrollmentOpen()).append("\n");
                debug.append("  Current Time: ").append(java.time.LocalDateTime.now()).append("\n\n");
            }
            
            // Check active period
            java.util.Optional<EnrollmentPeriod> activePeriod = enrollmentPeriodRepository.findActiveEnrollmentPeriod();
            debug.append("Active period found: ").append(activePeriod.isPresent()).append("\n");
            if (activePeriod.isPresent()) {
                debug.append("Active period semester: ").append(activePeriod.get().getSemester()).append("\n");
                debug.append("Active period academic year: ").append(activePeriod.get().getAcademicYear()).append("\n");
                debug.append("Active period is open: ").append(activePeriod.get().isEnrollmentOpen()).append("\n");
            }
            
            debug.append("\n=== CURRENT SEMESTER INFO ===\n");
            debug.append("Current Semester: ").append(getCurrentSemester()).append("\n");
            debug.append("Current Academic Year: ").append(getCurrentAcademicYear()).append("\n");
            
            return ResponseEntity.ok(debug.toString());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error getting debug info: " + e.getMessage());
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "admin-forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String adminId,
                                      RedirectAttributes redirectAttributes) {
        try {
            Optional<Admin> adminOpt = adminService.findAdminById(adminId);
            
            if (adminOpt.isPresent()) {
                // Generate a temporary password
                String tempPassword = generateTemporaryPassword();
                
                // Update admin with temporary password
                adminService.updateAdminPassword(adminId, tempPassword);
                
                // In a real application, you would send this via email
                // For this demo, we'll show it in a success message
                redirectAttributes.addFlashAttribute("success", 
                    "Password reset successful! Your temporary password is: " + tempPassword + 
                    ". Please login and change your password immediately.");
                
                return "redirect:/admin/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Admin ID not found");
                return "redirect:/admin/forgot-password";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing password reset: " + e.getMessage());
            return "redirect:/admin/forgot-password";
        }
    }
    
    private String generateTemporaryPassword() {
        // Generate a simple temporary password
        return "BMW" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMddHHmm"));
    }
    
    @GetMapping("/edit-admin/{adminId}")
    public String editAdminForm(@PathVariable String adminId, Model model, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            Optional<Admin> adminToEditOpt = adminService.findAdminById(adminId);
            if (adminToEditOpt.isPresent()) {
                model.addAttribute("admin", loggedInAdmin);
                model.addAttribute("adminToEdit", adminToEditOpt.get());
                return "admin-edit-admin";
            } else {
                model.addAttribute("error", "Admin not found");
                return "redirect:/admin/settings";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading admin: " + e.getMessage());
            return "redirect:/admin/settings";
        }
    }
    
    @PostMapping("/edit-admin/{adminId}")
    public String updateAdminDetails(@PathVariable String adminId,
                                   @RequestParam String firstName,
                                   @RequestParam String lastName,
                                   @RequestParam(required = false) String newPassword,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            Optional<Admin> adminToEditOpt = adminService.findAdminById(adminId);
            if (adminToEditOpt.isPresent()) {
                Admin adminToEdit = adminToEditOpt.get();
                
                // Update basic info
                adminToEdit.setFirstName(firstName);
                adminToEdit.setLastName(lastName);
                
                // Update password if provided
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    adminService.updateAdminPassword(adminId, newPassword);
                }
                
                // Save the admin (this will update firstName and lastName)
                adminService.saveAdmin(adminToEdit);
                
                redirectAttributes.addFlashAttribute("success", "Admin details updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Admin not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating admin: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }
} 